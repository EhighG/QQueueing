package com.example.tes24.qqueue_module.http.urlconnection;

import com.example.tes24.qqueue_module.http.Q2HttpHeader;

import java.net.HttpURLConnection;

public abstract class AbstractHttpURLConnectionFactory implements HttpURLConnectionFactory {
    @Override
    public HttpURLConnection getInstance(Q2HttpHeader q2HttpHeader) { return createHttpURLConnection(q2HttpHeader); }
    abstract protected HttpURLConnection createHttpURLConnection(Q2HttpHeader q2HttpHeader);
}
