package com.example.tes24.qqueue_module.adapter;

import com.example.tes24.qqueue_module.dto.Q2HttpHeader;

public class Q2AsyncAdapterFactoryImpl extends AbstractQ2AsyncAdapterFactory {
    private final HttpURLConnectionFactory httpURLConnectionFactory;

    public Q2AsyncAdapterFactoryImpl(HttpURLConnectionFactory httpURLConnectionFactory) {
        this.httpURLConnectionFactory = httpURLConnectionFactory;
    }
    @Override
    protected Q2AsyncAdapter createQ2Adapter(Q2HttpHeader q2HttpHeader) {
        return new Q2AsyncAdapterImpl(httpURLConnectionFactory.getInstance(q2HttpHeader));
    }
}
