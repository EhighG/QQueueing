package com.example.tes24.qqueue_module.adapter;

import com.example.tes24.qqueue_module.dto.Q2HttpHeader;

abstract class AbstractQ2SyncAdapterFactory implements Q2SyncAdapterFactory {
    @Override
    public Q2SyncAdapter getInstance(Q2HttpHeader httpHeaderInfo) {
        return createQ2Adapter(httpHeaderInfo);
    }

    abstract protected Q2SyncAdapter createQ2Adapter(Q2HttpHeader httpHeaderInfo);
}
