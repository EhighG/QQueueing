package com.example.tes24.service;

import com.example.tes24.dto.EnqueueResponse;
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

        return CompletableFuture.completedFuture(restClient.post().retrieve().body(EnqueueResponse.class));

//        Q2Adapter q2Adapter =
//                Q2AdapterBuilder.builder()
//                        .url("http://" + remoteUrl + ":" + port + "/tes24")
//                        .method("POST")
//                        .build();
//
//        Q2ClientRequest q2ClientRequest = new Q2ClientRequest();
//        q2ClientRequest.setClientId("1");
//        q2ClientRequest.setClientKey("2");
//        q2ClientRequest.setUserId("3");
//        q2ClientRequest.setUserKey("4");
//j
//        try {
//            Q2ServerResponse q2ServerResponse = q2Adapter.enqueue(q2ClientRequest);
//            log.info(String.valueOf(q2ServerResponse));
//        } catch (IOException e) {
//            log.error(e.getMessage());
//        }

//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {}
//        return CompletableFuture.completedFuture(new EnqueueResponse(0L, "id"));
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
