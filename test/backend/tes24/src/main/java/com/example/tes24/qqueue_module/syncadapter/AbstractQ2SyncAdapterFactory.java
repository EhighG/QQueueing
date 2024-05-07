package com.example.tes24.qqueue_module.syncadapter;

import com.example.tes24.qqueue_module.http.Q2HttpHeader;

abstract class AbstractQ2SyncAdapterFactory implements Q2SyncAdapterFactory {
    @Override
    public Q2SyncAdapter getInstance(Q2HttpHeader q2HttpHeader) {
        return createQ2Adapter(q2HttpHeader);
    }

    abstract protected Q2SyncAdapter createQ2Adapter(Q2HttpHeader q2HttpHeader);
}
