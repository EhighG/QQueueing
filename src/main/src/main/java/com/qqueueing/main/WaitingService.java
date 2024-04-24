package com.qqueueing.main;


import com.qqueueing.main.connect.ConsumerConnector;
import com.qqueueing.main.connect.ProducerConnector;

import com.qqueueing.main.model.GetMyOrderResDto;
import com.qqueueing.main.model.TestDto;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Transactional
@Service
public class WaitingService {

    private final ConsumerConnector consumerConnector;
    private final ProducerConnector producerConnector;
    private final String TARGET_URL;
    private Set<String> doneSet = new HashSet<>();
    private List<Long> outList = new LinkedList<>();
    private Integer batchLastIdx = 2;
    private Long target_capacity = 2000L;
    private Integer totalQueueSize = 5000;
    private Long tmpEnterCnt = 0L;


    public WaitingService(ConsumerConnector consumerConnector, ProducerConnector producerConnector,
                          @Value("${servers.target.url}") String targetApiUrl) {
        this.consumerConnector = consumerConnector;
        this.producerConnector = producerConnector;
        this.TARGET_URL = targetApiUrl;
    }

    public TestDto enter(String clientIp) {
        tmpEnterCnt++;

        // 카프카에 요청자 Ip 저장
        return producerConnector.enter(clientIp);
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

    public GetMyOrderResDto getMyOrder(Long oldOrder, String ip) {
        // 변경된 로직
        if (doneSet.contains(ip)) {
            doneSet.remove(ip);
//                response.sendRedirect(TARGET_URL);
            return new GetMyOrderResDto(0L, -1, TARGET_URL);
//            try {
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
        }
        int outCntInFront = - (Collections.binarySearch(outList, oldOrder) + 1);
        Long myOrder = oldOrder - outCntInFront - batchLastIdx; // newOrder
        return new GetMyOrderResDto(myOrder, totalQueueSize);
    }

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
