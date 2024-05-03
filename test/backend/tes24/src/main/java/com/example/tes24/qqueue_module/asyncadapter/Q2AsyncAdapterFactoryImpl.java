package com.example.tes24.qqueue_module.asyncadapter;

import com.example.tes24.qqueue_module.http.Q2HttpHeader;
import com.example.tes24.qqueue_module.http.urlconnection.HttpURLConnectionFactory;

public class Q2AsyncAdapterFactoryImpl extends AbstractQ2AsyncAdapterFactory {
    private final HttpURLConnectionFactory httpURLConnectionFactory;

    public Q2AsyncAdapterFactoryImpl(HttpURLConnectionFactory httpURLConnectionFactory) {
        this.httpURLConnectionFactory = httpURLConnectionFactory;
    }
    @Override
    protected Q2AsyncAdapter createQ2Adapter(Q2HttpHeader q2HttpHeader) {
        return new Q2AsyncAdapterImpl(q2HttpHeader);
    }
}
