package com.example.tes24.qqueue_module.adapter;

import com.example.tes24.qqueue_module.dto.Q2HttpHeader;

public class Q2SyncAdapterFactoryImpl extends AbstractQ2SyncAdapterFactory {
    private final HttpURLConnectionFactory httpURLConnectionFactory;

    public Q2SyncAdapterFactoryImpl(HttpURLConnectionFactory httpURLConnectionFactory) {
        this.httpURLConnectionFactory = httpURLConnectionFactory;
    }

    @Override
    protected Q2SyncAdapter createQ2Adapter(Q2HttpHeader q2HttpHeader) {
        return new Q2SyncAdapterImpl(httpURLConnectionFactory.getInstance(q2HttpHeader));
    }
}
