package com.example.tes24.service;

import com.example.tes24.dto.EnqueueResponse;
import com.example.tes24.qqueueing.Q2Client;
import com.example.tes24.qqueueing.context.beanfactory.Q2Context;
import com.example.tes24.qqueueing.dto.Q2ClientRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.concurrent.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueServiceImpl implements QueueService {
    @Value("${qqueue.server-url}")
    private String remoteUrl;

    private Q2Client q2Client = (Q2Client) Q2Context.getInstance().get(Q2Client.class);

    @Override
    @Async
    public Future<EnqueueResponse> enqueue() {
        Q2ClientRequest q2ClientRequest = new Q2ClientRequest();
        try {
            log.info(String.valueOf(q2Client.requestAsync(q2ClientRequest).get()));
        } catch (Exception e) {
            log.info(e.getMessage());
        }

        RestClient restClient = RestClient.builder()
                .requestFactory(new HttpComponentsClientHttpRequestFactory())
                .baseUrl("https://" + remoteUrl + "/product/1")
//                .defaultUriVariables(Map.of("memberId", memberId))
//                .defaultHeader("Authorization", "Bearer " + token)
                .build();
        return CompletableFuture.completedFuture(restClient.get().retrieve().body(EnqueueResponse.class));
    }

    @Override
    public ResponseEntity<?> dequeue() {
        RestClient restClient = RestClient.builder()
                .requestFactory(new HttpComponentsClientHttpRequestFactory())
//                .baseUrl("http://" + remoteUrl + ":" + port + "dequeue api")
                .build();

        return restClient.delete().retrieve().toEntity(String.class);
    }
}
