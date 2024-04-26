package com.qqueueing.main.waiting.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
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
        this.CONSUMER_ORIGIN = "http://" + CONSUMER_ORIGIN;
    }

    public Map<String, Object> getNext() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
            HttpEntity<?> requestEntity = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate
                    .postForEntity(CONSUMER_ORIGIN + "/consume", requestEntity, Map.class);
            System.out.println("response.getBody() = " + response.getBody());
            Map<String, Object> body = (Map<String, Object>) response.getBody().get("result");
            return body;
//            ResponseEntity<Map> response = restTemplate
//                    .postForEntity(CONSUMER_ORIGIN + "/consume", requestEntity, Map.class);
//
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
