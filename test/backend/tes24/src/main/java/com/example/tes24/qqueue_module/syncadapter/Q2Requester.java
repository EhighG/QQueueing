package com.example.tes24.qqueue_module.syncadapter;

import com.example.tes24.qqueue_module.dto.Q2ClientRequest;

import java.net.URLConnection;

public interface Q2Requester {
    void request(Q2ClientRequest q2ClientRequest, URLConnection urlConnection);
    boolean support(String contentTypeValue);
}
