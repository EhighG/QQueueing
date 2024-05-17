package com.example.tes24.qqueueing.adapter.async;

import com.example.tes24.qqueueing.dto.Q2ClientRequest;
import com.example.tes24.qqueueing.dto.Q2ServerResponse;

import java.util.concurrent.CompletableFuture;

public interface Q2AsyncAdapter {
    CompletableFuture<Q2ServerResponse> requestAsync(Q2ClientRequest q2ClientRequest);
}
