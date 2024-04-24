package com.example.tes24.service;

import com.example.tes24.dto.WaitingStatusResponseRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class MainServiceImpl implements MainService {
    @Value("${qqueue.server-url}")
    private String remoteUrl;

    @Value("${qqueue.server-port}")
    private String port;

    @Override
    public ResponseEntity<WaitingStatusResponseRecord> getWaitingStatus(Long waitingIdx, String idVal) {
        RestClient restClient = RestClient.builder()
                .requestFactory(new HttpComponentsClientHttpRequestFactory())
                .baseUrl("http://" + remoteUrl + ":" + port + "/waiting/" + waitingIdx)
//                .defaultUriVariables(Map.of("memberId", memberId))
//                .defaultHeader("Authorization", "Bearer " + token)
                .build();

        return restClient.post().body(idVal).retrieve().toEntity(WaitingStatusResponseRecord.class);
    }
}
