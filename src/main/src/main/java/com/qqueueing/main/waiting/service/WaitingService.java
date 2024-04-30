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

    private final KafkaTopicManager kafkaTopicManager;
    private Set<String> activeTopicNames = new HashSet<>();
    private Map<String, WaitingStatusDto> queues = new HashMap<>();

    public WaitingService(ConsumerConnector consumerConnector, TargetApiConnector targetApiConnector, EnterProducer enterProducer, RegistrationRepository registrationRepository, KafkaTopicManager kafkaTopicManager) {
        this.consumerConnector = consumerConnector;
        this.targetApiConnector = targetApiConnector;
        this.enterProducer = enterProducer;
        this.registrationRepository = registrationRepository;
        this.kafkaTopicManager = kafkaTopicManager;
    }

    @PostConstruct
    public void initQueues() {
        List<Registration> queues = registrationRepository.findAll();
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

    /**
     * 대기열 실행 중 상태 변경 시, 호출 필요
     */
    public void activate(Registration registration) {
        String topicName = registration.getTopicName();
        activeTopicNames.add(topicName);
        queues.put(topicName, new WaitingStatusDto(topicName, registration.getTargetUrl(), 0, 0));
        // re-init kafka topic
        kafkaTopicManager.deleteTopic(topicName);
        kafkaTopicManager.createTopic(topicName);
    }

    public void activate(String topicName) {
        Registration registration = registrationRepository.findByTopicName(topicName);
        activate(registration);
    }

    /**
     * 대기열 실행 중 상태 변경 시, 호출 필요
     * @param topicName
     */
    public void deactivate(String topicName) {
        if (!activeTopicNames.contains(topicName)) {
            return;
        }
        activeTopicNames.remove(topicName);
        queues.remove(topicName);
        kafkaTopicManager.deleteTopic(topicName);
    }

//    // 테스트용 메소드 : 대기열 등록, 삭제
//    public Set<String> addQueue(String topicName, String targetUrl) {
//        // 이미 있을 때
//        Registration registration = registrationRepository.findByTopicName(topicName);
//
//        if (registration != null) {
//            if (registration.getIsActive()) {
//                return activeTopicNames;
//            }
//            registration.setIsActive(true);
//        } else {
//            registration = new Registration(topicName, targetUrl, true);
//        }
//        activate(registration);
//        registrationRepository.save(registration); // save or update
//        return activeTopicNames;
//    }

    public Object enter(String topicName, HttpServletRequest request) {
        Registration registration = registrationRepository.findByTopicName(topicName);
        if (!activeTopicNames.contains(topicName)) {
            return targetApiConnector.forwardToTarget(registration.getTargetUrl(), request);
        }
        // 카프카에 요청자 Ip 저장
        return enterProducer.send(topicName, extractClientIp(request));
    }

    private String extractClientIp(HttpServletRequest request) {
        // 요청이 프록시 등을 거쳤을 때, 원래 ip주소를 담는 헤더
        String ip = request.getHeader("X-Forwarded-For");
        return ip != null ? ip : request.getRemoteAddr();
    }

    public void out(String topicName, Long order) {
        // outList는 '내 앞에 나간 사람 수' 를 알기 위해 쓰이므로, 정렬된 채로 유지
        try {
            List<Long> outList = queues.get(topicName).getOutList();
            int insertIdx = -(Collections.binarySearch(outList, order) + 1);
            outList.add(insertIdx, order);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object getMyOrder(String topicName, Long oldOrder, String ip, HttpServletRequest request) {
        WaitingStatusDto waitingStatus = queues.get(topicName);
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
        if (activeTopicNames.isEmpty()) {
            return;
        }
        Map<String, BatchResDto> response = consumerConnector.getNext(activeTopicNames); // 대기 완료된 ip 목록을 가져온다.
        for (String topicName : response.keySet()) {
            WaitingStatusDto waitingStatus = queues.get(topicName);
            BatchResDto batchRes = response.get(topicName);

            waitingStatus.getDoneSet()
                    .addAll(batchRes.getCurDoneList());
            waitingStatus.setLastOffset(batchRes.getLastOffset());
            waitingStatus.setTotalQueueSize(batchRes.getTotalQueueSize());
        }
        cleanUpOutList();
    }

    /**
     * 대기 끝난 사람들 삭제
     */
    private void cleanUpOutList() {
        for (String topicName : queues.keySet()) {
            WaitingStatusDto waigtingStatus = queues.get(topicName);
            int lastOffset = waigtingStatus.getLastOffset();
            waigtingStatus.setOutList(
                    waigtingStatus.getOutList().stream()
                            .filter(i -> i > lastOffset)
                            .toList()
            );
        }
    }
}
