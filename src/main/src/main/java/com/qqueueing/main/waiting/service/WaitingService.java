package com.qqueueing.main.waiting.service;


import com.qqueueing.main.waiting.model.GetMyOrderResDto;
import com.qqueueing.main.waiting.model.TestDto;
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
    private final String TARGET_URL;
    private Set<String> doneSet = new HashSet<>();
    private List<Long> outList = new LinkedList<>();
    private Integer batchLastIdx = 2;
    private Long target_capacity = 2000L;
    private Integer totalQueueSize = 5000;
    private Long tmpEnterCnt = 0L;


    public WaitingService(ConsumerConnector consumerConnector, TargetApiConnector targetApiConnector, EnterProducer enterProducer,
                          @Value("${servers.target.url}") String targetApiUrl) {
        this.consumerConnector = consumerConnector;
        this.targetApiConnector = targetApiConnector;
        this.enterProducer = enterProducer;
//        this.producerConnector = producerConnector;
        this.TARGET_URL = targetApiUrl;
    }

    public TestDto enter(String clientIp) {
        tmpEnterCnt++;

        // 카프카에 요청자 Ip 저장
        return enterProducer.send(clientIp);
    }

    public void out(Long order) {
        // outList는 '내 앞에 나간 사람 수' 를 알기 위해 쓰이므로, 정렬된 채로 유지
        try {

            int insertIdx = - (Collections.binarySearch(outList, order) + 1);
            outList.add(insertIdx, order);
            LinkedList<Integer> tl = new LinkedList<>();
            int a = 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object getMyOrder(Long oldOrder, String ip, HttpServletRequest request) {
        // 변경된 로직
        if (doneSet.contains(ip)) {
            doneSet.remove(ip);
            return targetApiConnector.forwardToTarget(request); // forwarding
        }
        int outCntInFront = - (Collections.binarySearch(outList, oldOrder) + 1);
        Long myOrder = oldOrder - outCntInFront - batchLastIdx; // newOrder
        return new GetMyOrderResDto(myOrder, totalQueueSize);
    }


//    public ResponseEntity<?> test(HttpServletRequest request) {
//        return targetApiConnector.forwardToTarget(request);
//    }

    @Async
    @Scheduled(cron = "0/5 * * * * *") // 매 분 0초부터, 3초마다
    public void getNext() {
        Map<String, Object> batchRes = consumerConnector.getNext(); // 대기 완료된 ip 목록을 가져온다.

        doneSet.addAll((List<String>)batchRes.get("curDoneSet"));
//        System.out.println("doneSet = " + doneSet);

        // 변경된 로직
        batchLastIdx = (Integer) batchRes.get("batchLastIdx");
        totalQueueSize = (Integer) batchRes.get("totalQueueSize");

        cleanUpOutList(); // 대기 끝난 애들 빼기
    }

    private void cleanUpOutList() {
        outList = outList.stream()
                .filter(i -> i > batchLastIdx)
                .collect(Collectors.toList());
    }

}
