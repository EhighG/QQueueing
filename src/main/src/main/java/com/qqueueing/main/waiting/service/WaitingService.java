package com.qqueueing.main.waiting.service;


import com.qqueueing.main.registration.model.Registration;
import com.qqueueing.main.registration.repository.RegistrationRepository;
import com.qqueueing.main.waiting.model.BatchResDto;
import com.qqueueing.main.waiting.model.GetMyOrderResDto;
import com.qqueueing.main.waiting.model.WaitingStatusDto;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Slf4j
@Transactional
@Service
public class WaitingService {

    private final ConsumerConnector consumerConnector;
    private final TargetApiConnector targetApiConnector;
    private final EnterProducer enterProducer;
    private final RegistrationRepository registrationRepository;

    private Set<Integer> activePartitions = new HashSet<>();
    private Map<Integer, WaitingStatusDto> queues = new HashMap<>();
    private Map<String, Integer> partitionNoMapper = new HashMap<>();
    private Map<String, String> targetUrlMapper = new HashMap<>();
    private final int TOKEN_LEN = 20;

    public WaitingService(ConsumerConnector consumerConnector, TargetApiConnector targetApiConnector,
                          EnterProducer enterProducer, RegistrationRepository registrationRepository){
        this.consumerConnector = consumerConnector;
        this.targetApiConnector = targetApiConnector;
        this.enterProducer = enterProducer;
        this.registrationRepository = registrationRepository;
    }

    /**
     * 대기열 서버가 비정상 종료 후 다시 켜질 때, 대기열을 다시 활성화
     */
    @PostConstruct
    public void initQueues() {
        List<Registration> queues = registrationRepository.findAll();
        queues.forEach(r -> {
                    partitionNoMapper.put(r.getTargetUrl(), r.getPartitionNo());
                    if (r.getIsActive()) activate(r);
                });
    }

    public void addUrlPartitionMapping(String targetUrl) {
        Registration registration = registrationRepository.findByTargetUrl(targetUrl);
        if (registration != null) {
            partitionNoMapper.put(registration.getTargetUrl(), registration.getPartitionNo());
        }
    }

    /**
     * 대기열 실행 중 상태 변경 시, 호출 필요
     */
    public void activate(Registration registration) {
        int partitionNo = registration.getPartitionNo();
        activePartitions.add(partitionNo);
        queues.put(partitionNo, new WaitingStatusDto(partitionNo, registration.getTargetUrl(), 0, 0));
        // update mongodb data
        registration.setIsActive(true);
        registrationRepository.save(registration);
//        // re-init partition
//        consumerConnector.clearPartition(partitionNo);
        log.info("activation end");
//        // re-init auto increment client order
//        enterProducer.activate(partitionNo);
    }

    public void activate(int partitionNo) {
        Registration registration = registrationRepository.findByPartitionNo(partitionNo);
        activate(registration);
    }

    /**
     * 대기열 실행 중 상태 변경 시, 호출 필요
     * @param partitionNo
     */
    public void deactivate(int partitionNo) {
        if (!activePartitions.contains(partitionNo)) {
            return;
        }
        Registration registration = registrationRepository.findByPartitionNo(partitionNo);

        activePartitions.remove(partitionNo);
        queues.remove(partitionNo);

        // update mongodb data
        registration.setIsActive(false);
        registrationRepository.save(registration);
    }

    public Object enter(HttpServletRequest request) {
        String requestUrl = request.getHeader("Target-Url");
        if (requestUrl == null) requestUrl = request.getRequestURL().toString();
        log.info("requestUrl = {}", requestUrl);
        int partitionNo = partitionNoMapper.get(requestUrl);

        if (activePartitions.contains(partitionNo)) {
            // 카프카에 요청자 Ip 저장
            return enterProducer.send(extractClientIp(request), partitionNo);
        }
        // 대기열 비활성화 상태 -> null 반환 후, 컨트롤러에서 대기열 페이지로 리다이렉트 응답
        return createTempToken(requestUrl);
    }

    private String createTempToken(String targetUrl) {
        String tempToken = RandomStringUtils.randomAlphanumeric(TOKEN_LEN);
        while (targetUrlMapper.get(tempToken) != null) {
            tempToken = RandomStringUtils.randomAlphanumeric(TOKEN_LEN);
        }
        targetUrlMapper.put(targetUrl, tempToken);
        return tempToken;
    }

    private String extractClientIp(HttpServletRequest request) {
        // 요청이 프록시 등을 거쳤을 때, 원래 ip주소를 담는 헤더
        String ip = request.getHeader("X-Forwarded-For");
        log.info("client ip(X-Forwarded-For) = {}", ip);
        if (ip != null) return ip;

        String remoteAddr = request.getRemoteAddr();
        log.info("request.remoteAddr = {}", remoteAddr);
        return remoteAddr;
    }

    public void out(int partitionNo, Long order) {
        // outList는 '내 앞에 나간 사람 수' 를 알기 위해 쓰이므로, 정렬된 채로 유지
        try {
            List<Long> outList = queues.get(partitionNo).getOutList();
            int insertIdx = -(Collections.binarySearch(outList, order) + 1);
            outList.add(insertIdx, order);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GetMyOrderResDto getMyOrder(int partitionNo, Long oldOrder, String ip, HttpServletRequest request) {
        WaitingStatusDto waitingStatus = queues.get(partitionNo);
        Set<String> doneSet = waitingStatus.getDoneSet();
        List<Long> outList = waitingStatus.getOutList();
        int lastOffset = waitingStatus.getLastOffset();
        int outCntInFront = - (Collections.binarySearch(outList, oldOrder) + 1);
        Long myOrder = oldOrder - outCntInFront - lastOffset; // newOrder
        GetMyOrderResDto result = new GetMyOrderResDto(myOrder, waitingStatus.getTotalQueueSize());
        if (doneSet.contains(ip)) { // waiting done
            doneSet.remove(ip);
            result.setTempToken(createTempToken(waitingStatus.getTargetUrl()));
        }
        return result;
    }

    /**
     * 타겟 프론트 페이지 포워딩 메소드
     */
    public ResponseEntity<String> forwardToTarget(String token, HttpServletRequest request) {
        String targetUrl = targetUrlMapper.get(token);
        if (targetUrl == null) {
            throw new IllegalArgumentException("invalid token");
        }
        targetUrlMapper.remove(token);
        /*
            파싱 로직 추가 위치
         */
        return targetApiConnector.forwardToTarget(token, request);
    }

    @Async
    @Scheduled(cron = "0/5 * * * * *") // 매 분 0초부터, 5초마다
    public void getNext() {
        try {
            if (activePartitions.isEmpty()) {
                return;
            }
            Map<Integer, BatchResDto> response = consumerConnector.getNext(activePartitions); // 대기 완료된 ip 목록을 가져온다.
            System.out.println("response = " + response);
            Set<Integer> partitionNos = response.keySet();

            for (Integer partitionNo : partitionNos) {
                WaitingStatusDto waitingStatus = queues.get(partitionNo);

                BatchResDto batchRes = response.get(partitionNo);
                waitingStatus.getDoneSet()
                        .addAll(batchRes.getCurDoneList());
                waitingStatus.setLastOffset(batchRes.getLastOffset());
                waitingStatus.setTotalQueueSize(batchRes.getTotalQueueSize());
            }
            cleanUpOutList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 대기 끝난 사람들 삭제
     */
    private void cleanUpOutList() {
        for (int partitionNo : queues.keySet()) {
            WaitingStatusDto waigtingStatus = queues.get(partitionNo);
            int lastOffset = waigtingStatus.getLastOffset();
            waigtingStatus.setOutList(
                    waigtingStatus.getOutList().stream()
                            .filter(i -> i > lastOffset)
                            .toList()
            );
        }
    }
}
