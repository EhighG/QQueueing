package com.example.tes24.service;

import com.example.tes24.dto.EnqueueResponseRecord;
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

    @Value("${qqueue.server-port}")
    private String port;

    @Override
    public ResponseEntity<EnqueueResponseRecord> enqueue(Long memberId) {
        RestClient restClient = RestClient.builder()
                .requestFactory(new HttpComponentsClientHttpRequestFactory())
                .baseUrl("http://" + remoteUrl + ":" + port + "/waiting")
//                .defaultUriVariables(Map.of("memberId", memberId))
//                .defaultHeader("Authorization", "Bearer " + token)
                .build();

        return restClient.post().retrieve().toEntity(EnqueueResponseRecord.class);
    }

    @Override
    public ResponseEntity<?> dequeue() {
        RestClient restClient = RestClient.builder()
                .requestFactory(new HttpComponentsClientHttpRequestFactory())
                .baseUrl("http://" + remoteUrl + ":" + port + "dequeue api")
                .build();

        return restClient.delete().retrieve().toEntity(String.class);
    }
}
