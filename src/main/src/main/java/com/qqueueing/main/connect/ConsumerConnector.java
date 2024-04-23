package com.practice.apiserver.connect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
//@RequiredArgsConstructor
@Component
public class ConsumerConnector {

    private final RestTemplate restTemplate;
    private final String CONSUMER_ORIGIN;

    public ConsumerConnector(RestTemplate restTemplate,
                             @Value("${servers.consumer}") String CONSUMER_ORIGIN) {
        this.restTemplate = restTemplate;
        this.CONSUMER_ORIGIN = CONSUMER_ORIGIN;
    }

    public List<Long> updateOrders() {
        try {
            List body = restTemplate.getForEntity(CONSUMER_ORIGIN + "컨슈머 서버의 api url", List.class).getBody();
            return (List<Long>) body;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
