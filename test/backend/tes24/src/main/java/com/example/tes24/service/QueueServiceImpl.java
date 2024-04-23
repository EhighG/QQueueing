package com.example.tes24.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class QueueServiceImpl implements QueueService {
    @Value("${qqueue.server-url}")
    private String remoteUrl;

    @Override
    public ResponseEntity<String> enqueue(Long memberId) {
        RestClient restClient = RestClient.builder()
                .requestFactory(new HttpComponentsClientHttpRequestFactory())
                .baseUrl("https://" + remoteUrl + "enqueue api")
                .defaultUriVariables(Map.of("memberId", memberId))
//                .defaultHeader("Authorization", "Bearer " + token)
                .build();

        return restClient.post().retrieve().toEntity(String.class);
    }

    @Override
    public ResponseEntity<String> dequeue() {
        RestClient restClient = RestClient.builder()
                .requestFactory(new HttpComponentsClientHttpRequestFactory())
                .baseUrl("https://" + remoteUrl + "dequeue api")
                .build();

        return restClient.delete().retrieve().toEntity(String.class);
    }
}
