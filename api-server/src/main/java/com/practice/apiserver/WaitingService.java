package com.practice.apiserver;

import com.practice.apiserver.connect.ConsumerConnector;
import com.practice.apiserver.connect.ProducerConnector;
import com.practice.apiserver.sse.WaitingInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Transactional
@Service
public class WaitingService {

    private final ConsumerConnector consumerConnector;
    private final ProducerConnector producerConnector;
    private final Long TARGET_CAPACITY;
    private final String TARGET_URL;

    public WaitingService(ConsumerConnector consumerConnector, ProducerConnector producerConnector,
                          @Value("${servers.target.traffic-capacity}") Long targetCapacity,
                          @Value("${servers.target.url") String targetApiUrl) {
        this.consumerConnector = consumerConnector;
        this.producerConnector = producerConnector;
        this.TARGET_CAPACITY = targetCapacity;
        this.TARGET_URL = targetApiUrl;
    }

    private Map<Integer, Long> waitingOrders = new HashMap<>();

    public Long enter(String clientIp) {
        // 카프카에 요청자 Ip 저장
        return producerConnector.enter(clientIp);
    }

    public void out(Long userIdx) {
        producerConnector.out(userIdx);
    }

    public WaitingInfo getMyOrder(HttpServletResponse response, Long userIdx) {
        Long order = waitingOrders.get(userIdx);
        // 순서가 됐으면, redirect시키고 out 토픽에 추가한다.
        if (order < TARGET_CAPACITY) { // 0-based
            try {
                response.sendRedirect(TARGET_URL);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return WaitingInfo
                .builder()
                .myOrder(order)
                .totalNum(waitingOrders.size())
                .build();
    }

    @Async // 비동기로 실행
    @Scheduled(cron = "0/3 * * * * *") // 매 분 0초부터, 3초마다
    public void updateOrders() {
        List<Long> recentList = consumerConnector.updateOrders();
        int end = recentList.size() + 1;

        // 대기순서 정보 업데이트
        waitingOrders = IntStream.range(1, end)
                .boxed()
                .collect(Collectors.toMap(
                        i -> i - 1,
                        recentList::get
                ));
    }

}
