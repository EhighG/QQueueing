package com.qqueueing.main.waiting.service;

import com.qqueueing.main.waiting.model.BatchResDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class ConsumerConnector {

    private final RestTemplate restTemplate;
    private final String CONSUMER_ORIGIN;

    public ConsumerConnector(RestTemplate restTemplate,
                             @Value("${servers.consumer}") String CONSUMER_ORIGIN) {
        this.restTemplate = restTemplate;
        this.CONSUMER_ORIGIN = "http://" + CONSUMER_ORIGIN;
    }

    public Map<Integer, BatchResDto> getNext(Set<Integer> activePartitions) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
            HttpEntity<?> requestEntity = new HttpEntity<>(activePartitions, headers);
            ResponseEntity<Map<Integer, BatchResDto>> response = restTemplate.exchange(
                    CONSUMER_ORIGIN + "/consume", // 요청 URL
                    HttpMethod.POST, // HTTP 메서드
                    requestEntity, // 요청 헤더와 본문을 포함한 HttpEntity 객체
                    ParameterizedTypeReference.forType(Map.class));
            System.out.println("response.getBody() = " + response.getBody());
            return response.getBody();
        } catch (Exception e) {
//            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
