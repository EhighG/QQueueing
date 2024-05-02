package com.example.tes24.qqueue_module.http.urlconnection;

import com.example.tes24.qqueue_module.http.Q2HttpHeader;

import java.net.HttpURLConnection;

public interface HttpURLConnectionFactory {
    HttpURLConnection getInstance(Q2HttpHeader q2HttpHeader);
}
