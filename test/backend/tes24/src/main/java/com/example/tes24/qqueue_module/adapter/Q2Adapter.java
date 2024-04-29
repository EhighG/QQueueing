package com.example.tes24.qqueue_module.adapter;

import com.example.tes24.qqueue_module.dto.Q2ClientRequest;
import com.example.tes24.qqueue_module.dto.Q2ServerResponse;

import java.io.IOException;

public interface Q2Adapter {
    Q2ServerResponse enqueue(Q2ClientRequest q2ClientRequest);
}
