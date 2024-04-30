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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Transactional
@Service
public class WaitingService {

    private final ConsumerConnector consumerConnector;
    private final TargetApiConnector targetApiConnector;
    private final EnterProducer enterProducer;
    private final RegistrationRepository registrationRepository;

//    private final KafkaTopicManager kafkaTopicManager;
    private final int MAX_PARTITION_INDEX;
    private Set<Integer> activePartitions = new HashSet<>();
    private Map<Integer, WaitingStatusDto> queues = new HashMap<>();

    public WaitingService(ConsumerConnector consumerConnector, TargetApiConnector targetApiConnector,
                          EnterProducer enterProducer, RegistrationRepository registrationRepository,
//                          KafkaTopicManager kafkaTopicManager,
                          @Value("${kafka.partition.max-index}") int maxPartitionIndex){
        this.consumerConnector = consumerConnector;
        this.targetApiConnector = targetApiConnector;
        this.enterProducer = enterProducer;
        this.registrationRepository = registrationRepository;
//        this.kafkaTopicManager = kafkaTopicManager;
        this.MAX_PARTITION_INDEX = maxPartitionIndex;
    }

    @PostConstruct
    public void initQueues() {
        List<Registration> queues = registrationRepository.findAll();
//        if (queues == null || queues.isEmpty()) return;
        queues.stream()
                .filter(Registration::getIsActive) // isActive 상태 조작은 대기열 활성화 시점과는 별개
                .forEach(this::activate);
    }

    @PreDestroy
    public void offQueues() {
//        List<Registration> queues = registrationRepository.findAll();
//        queues.stream()
//                .filter(Registration::isActive) // isActive 상태 조작은 대기열 활성화 시점과는 별개
//                .map(Registration::getTopicName)
//                .forEach(kafkaTopicManager::deleteTopic);
    }

    public int findEmptyPartitionNo() {
        Set<Integer> assigned = registrationRepository.findAll().stream()
                .map(Registration::getPartitionNo)
                .collect(Collectors.toSet());
        for (int i = 0; i < MAX_PARTITION_INDEX; i++) {
            if (!assigned.contains(i)) {
                return i;
            }
        }
        // 모든 파티션이 사용중일 때
        throw new RuntimeException("대기열을 더 이상 추가할 수 없습니다.");
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

//        // re-init kafka topic
//        kafkaTopicManager.deleteTopic(partitionNo);
//        kafkaTopicManager.createTopic(partitionNo);
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
//        kafkaTopicManager.deleteTopic(partitionNo);

        // update mongodb data
        registration.setIsActive(false);
        registrationRepository.save(registration);
    }

//    // 테스트용 메소드 : 대기열 등록, 삭제
//    public Set<String> addQueue(int partitionNo, String targetUrl) {
//        // 이미 있을 때
//        Registration registration = registrationRepository.findByPartitionNo(partitionNo);)
//
//        if (registration != null) {
//            if (registration.getIsActive()) {
//                return activePartitions;
//            }
//            registration.setIsActive(true);
//        } else {
//            registration = new Registration(partitionNo, targetUrl, true);
//        }
//        activate(registration);
//        registrationRepository.save(registration); // save or update
//        return activePartitions;
//    }

    public Object enter(int partitionNo, HttpServletRequest request) {
        Registration registration = registrationRepository.findByPartitionNo(partitionNo);
        if (!activePartitions.contains(partitionNo)) {
            return targetApiConnector.forwardToTarget(registration.getTargetUrl(), request);
        }
        // 카프카에 요청자 Ip 저장
        return enterProducer.send(extractClientIp(request), partitionNo);
    }

    private String extractClientIp(HttpServletRequest request) {
        // 요청이 프록시 등을 거쳤을 때, 원래 ip주소를 담는 헤더
        String ip = request.getHeader("X-Forwarded-For");
        return ip != null ? ip : request.getRemoteAddr();
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
        List<Long> outList = waitingStatus.getOutList();
        int lastOffset = waitingStatus.getLastOffset();
        if (doneSet.contains(ip)) {
            doneSet.remove(ip);
            return targetApiConnector.forwardToTarget(waitingStatus.getTargetUrl(), request); // forwarding
        }
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
            for (int partitionNo : response.keySet()) {
                WaitingStatusDto waitingStatus = queues.get(partitionNo);
                BatchResDto batchRes = response.get(partitionNo);

                waitingStatus.getDoneSet()
                        .addAll(batchRes.getCurDoneList());
                waitingStatus.setLastOffset(batchRes.getLastOffset());
                waitingStatus.setTotalQueueSize(batchRes.getTotalQueueSize());
            }
            cleanUpOutList();
        } catch (Exception e) {
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
