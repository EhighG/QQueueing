package com.practice.apiserver.connect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
//@RequiredArgsConstructor
@Component
public class ProducerConnector {

    private final RestTemplate restTemplate;
    private final String PRODUCER_ORIGIN;

    public ProducerConnector(RestTemplate restTemplate,
                             @Value("${servers.producer}") String PRODUCER_ORIGIN) {
        this.restTemplate = restTemplate;
        this.PRODUCER_ORIGIN = PRODUCER_ORIGIN;
    }

    public Long enter(String clientIp) {
        ResponseEntity<Long> response = restTemplate.postForEntity(PRODUCER_ORIGIN + "/waiting", clientIp, Long.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("대기열 등록 중 에러");
        }
        return response.getBody();
    }

    public void out(Long enterTopicKey) {
        ResponseEntity<Void> response = restTemplate.getForEntity(PRODUCER_ORIGIN + "/waiting" + enterTopicKey + "/out", Void.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("대기열 나가기 중 에러");
        }
    }
}
