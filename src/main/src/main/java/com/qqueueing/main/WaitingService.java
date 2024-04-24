package com.qqueueing.main;


import com.qqueueing.main.connect.ConsumerConnector;
import com.qqueueing.main.connect.ProducerConnector;
import com.qqueueing.main.model.BatchResDto;
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


@Slf4j
@Transactional
@Service
public class WaitingService {

    private final ConsumerConnector consumerConnector;
    private final ProducerConnector producerConnector;
    private final String TARGET_URL;
    private Set<String> doneSet = new HashSet<>();
    private List<Long> outList = new LinkedList<>();
    private Long batchLastIdx = 2L;
    private Long target_capacity = 2000L;
    private Long totalQueueSize = 5000L;
    private Long tmpEnterCnt = 0L;


    public WaitingService(ConsumerConnector consumerConnector, ProducerConnector producerConnector,
                          @Value("${servers.target.url}") String targetApiUrl) {
        this.consumerConnector = consumerConnector;
        this.producerConnector = producerConnector;
        this.TARGET_URL = targetApiUrl;
    }

    public TestDto enter(String clientIp) {
//        if (totalQueueSize < target_capacity)
        // 카프카에 요청자 Ip 저장
        return new TestDto(producerConnector.enter(clientIp), clientIp);
    }

    public void out(Long order) {
        // outList는 '내 앞에 나간 사람 수' 를 알기 위해 쓰이므로, 정렬된 채로 유지
        int insertIdx = - (Collections.binarySearch(outList, order) + 1);
        outList.add(insertIdx, order);
    }

    public Long getMyOrder(HttpServletResponse response, Long oldOrder, String ip) {
        // 변경된 로직
        if (doneSet.contains(ip)) {
            try {
                doneSet.remove(ip);
                response.sendRedirect(TARGET_URL);
                return -1L;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        int outCntInFront = - (Collections.binarySearch(outList,oldOrder) + 1);
        return oldOrder - outCntInFront - batchLastIdx; // newOrder
    }

    @Async
    @Scheduled(cron = "0/3 * * * * *") // 매 분 0초부터, 3초마다
    public void getNext() {
        BatchResDto batchRes = consumerConnector.getNext(); // 대기 완료된 ip 목록을 가져온다.

        doneSet.addAll(new HashSet<>(batchRes.getCurDoneSet()));

        // 변경된 로직
        batchLastIdx = batchRes.getBatchLastIdx();
        totalQueueSize = batchRes.getTotalQueueSize();

        cleanUpOutList(); // 대기 끝난 애들 빼기
    }

    private void cleanUpOutList() {
        outList = outList.stream()
                .filter(i -> i > batchLastIdx)
                .toList();
    }

}
