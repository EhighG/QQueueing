package com.example.tes24.qqueue_module.asyncadapter;

import com.example.tes24.qqueue_module.dto.Q2ClientRequest;
import com.example.tes24.qqueue_module.dto.Q2ServerResponse;

import java.util.concurrent.Future;

public interface Q2AsyncAdapter {
    Future<Q2ServerResponse> enqueue(Q2ClientRequest q2ClientRequest);
}
