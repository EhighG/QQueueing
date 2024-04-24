package com.qqueueing.main.connect;

import com.qqueueing.main.model.BatchResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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

    public BatchResDto getNext() {
        try {
            ResponseEntity<Map> body = restTemplate.getForEntity(CONSUMER_ORIGIN + "컨슈머 서버의 api url", Map.class);
            return (BatchResDto) body.getBody().get("result");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
