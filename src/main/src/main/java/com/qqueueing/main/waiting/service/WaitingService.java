package com.qqueueing.main.waiting.service;


import com.qqueueing.main.registration.model.Registration;
import com.qqueueing.main.registration.repository.RegistrationRepository;
import com.qqueueing.main.waiting.model.BatchResDto;
import com.qqueueing.main.waiting.model.GetMyOrderResDto;
import com.qqueueing.main.waiting.model.WaitingStatusDto;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
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

    @PreDestroy
    public void offQueues() {
//        List<Registration> queues = registrationRepository.findAll();
//        queues.stream()
//                .filter(Registration::isActive) // isActive 상태 조작은 대기열 활성화 시점과는 별개
//                .map(Registration::getTopicName)
//                .forEach(kafkaTopicManager::deleteTopic);
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
        // re-init partition
        consumerConnector.clearPartition(partitionNo);
        log.info("activation end");
        // re-init auto increment client order
        enterProducer.activate(partitionNo);
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
//        // 대기열 비활성화 상태
//        Registration registration = registrationRepository.findByPartitionNo(partitionNo);
//        return targetApiConnector.forwardToTarget(registration.getTargetUrl(), request);
        // 대기열 비활성화 상태 -> null 반환 후, 컨트롤러에서 대기열 페이지로 리다이렉트 응답
        return null;
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

    public Object getMyOrder(int partitionNo, Long oldOrder, String ip, HttpServletRequest request) {
        WaitingStatusDto waitingStatus = queues.get(partitionNo);
        Set<String> doneSet = waitingStatus.getDoneSet();
        if (doneSet.contains(ip)) {
            doneSet.remove(ip);
            return targetApiConnector.forwardToTarget(waitingStatus.getTargetUrl(), request); // forwarding
        }
        List<Long> outList = waitingStatus.getOutList();
        int lastOffset = waitingStatus.getLastOffset();
        int outCntInFront = - (Collections.binarySearch(outList, oldOrder) + 1);
        Long myOrder = oldOrder - outCntInFront - lastOffset; // newOrder
        return new GetMyOrderResDto(myOrder, waitingStatus.getTotalQueueSize());
    }

    @Async
    @Scheduled(cron = "0/5 * * * * *") // 매 분 0초부터, 3초마다
    public void getNext() {
        try {
            if (activePartitions.isEmpty()) {
                return;
            }
            Map<Integer, BatchResDto> response = consumerConnector.getNext(activePartitions); // 대기 완료된 ip 목록을 가져온다.
            System.out.println("response = " + response);
            Set<Integer> partitionNoStrs = response.keySet();
//            Set<Integer> partitionNoStrs = response.keySet().stream()
//                            .map(Integer::parseInt)
//                                    .collect(Collectors.toSet());
            System.out.println("response.keySet() = " + partitionNoStrs);
            System.out.println("partitionNoStrs.getClass() = " + partitionNoStrs.getClass());
            System.out.println("response.keySet().getClass() = " + response.keySet().getClass());
//            for (int partitionNo : partitionNoStrs) {
            for (Integer partitionNo : partitionNoStrs) {
//                int partitionNo = Integer.parseInt(partitionNoStr);
                System.out.println("run " + partitionNo);
                WaitingStatusDto waitingStatus = queues.get(partitionNo);
//                waitingStatus.getDoneSet().add("test1");
//                System.out.println("Test1");
//                BatchResDto batchRes = response.get(partitionNoStr);
                BatchResDto batchRes = response.get(partitionNo);
                System.out.println("batchRes = " + batchRes);

//                waitingStatus.getDoneSet()
//                        .addAll((List) batchRes.get("curDoneList"));
//                waitingStatus.setLastOffset(Integer.parseInt(batchRes.get("lastOffset").toString()));
//                waitingStatus.setTotalQueueSize(Integer.parseInt(batchRes.get("totalQueueSize").toString()));
//                waitingStatus.getDoneSet().add("test2");
//                System.out.println("Test2");
                waitingStatus.getDoneSet()
                        .addAll(batchRes.getCurDoneList());
                waitingStatus.setLastOffset(batchRes.getLastOffset());
                waitingStatus.setTotalQueueSize(batchRes.getTotalQueueSize());
//                waitingStatus.getDoneSet().add("test2");
//                System.out.println("Test2");
            }
            cleanUpOutList();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(activePartitions);
            System.out.println(".");
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
