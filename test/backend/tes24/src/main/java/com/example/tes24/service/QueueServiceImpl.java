package com.example.tes24.service;

import com.example.tes24.dto.EnqueueResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class QueueServiceImpl implements QueueService {
    @Value("${qqueue.server-url}")
    private String remoteUrl;

    @Value("${qqueue.server-port}")
    private String port;

    @Override
    @Async
    public Future<EnqueueResponse> enqueue() {
        RestClient restClient = RestClient.builder()
                .requestFactory(new HttpComponentsClientHttpRequestFactory())
                .baseUrl("http://" + remoteUrl + ":" + port + "/waiting")
//                .defaultUriVariables(Map.of("memberId", memberId))
//                .defaultHeader("Authorization", "Bearer " + token)
                .build();

//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {}
//        return CompletableFuture.completedFuture(new EnqueueResponse(0L, "id"));

        return CompletableFuture.completedFuture(restClient.post().retrieve().body(EnqueueResponse.class));
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
