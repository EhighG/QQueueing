package com.example.tes24.qqueue_module.adapter;

import com.example.tes24.qqueue_module.dto.Q2HttpHeader;

import java.net.HttpURLConnection;

public interface HttpURLConnectionFactory {
    HttpURLConnection getInstance(Q2HttpHeader q2HttpHeader);
}
