package com.example.tes24.qqueue_module.syncadapter;

import com.example.tes24.qqueue_module.Q2Context;
import com.example.tes24.qqueue_module.http.Q2HttpHeader;
import com.example.tes24.qqueue_module.http.urlconnection.HttpURLConnectionFactory;

public final class Q2SyncAdapterFactoryImpl extends AbstractQ2SyncAdapterFactory {
    private final HttpURLConnectionFactory httpURLConnectionFactory;

    public Q2SyncAdapterFactoryImpl(HttpURLConnectionFactory httpURLConnectionFactory) {
        this.httpURLConnectionFactory = httpURLConnectionFactory;
    }

    @Override
    protected Q2SyncAdapter createQ2Adapter(Q2HttpHeader q2HttpHeader) {
        return new Q2SyncAdapterImpl(httpURLConnectionFactory.getInstance(q2HttpHeader), Q2Context.getQ2Requesters());
    }
}
