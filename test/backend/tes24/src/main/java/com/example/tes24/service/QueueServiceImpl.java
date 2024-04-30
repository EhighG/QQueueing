package com.example.tes24.service;

import com.example.tes24.dto.EnqueueResponse;
import com.example.tes24.qqueue_module.adapter.Q2Client;
import com.example.tes24.qqueue_module.dto.Q2ClientRequest;
import com.example.tes24.qqueue_module.dto.Q2HttpHeader;
import com.example.tes24.qqueue_module.dto.Q2ServerResponse;
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

//        Q2ClientRequest request = new Q2ClientRequest();
//        request.setClientId("1");
//        request.setClientKey("2");
//        request.setUserId("3");
//        request.setUserKey("4");
//
//        Q2Client q2Client = Q2Client.getQ2Client();
//        Q2ServerResponse response = q2Client.request(Q2HttpHeader.defaultQ2HttpHeader(), request);
//        log.info(response.toString());
//
//        return CompletableFuture.completedFuture(new EnqueueResponse(-1L, response.getClientId()));
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
