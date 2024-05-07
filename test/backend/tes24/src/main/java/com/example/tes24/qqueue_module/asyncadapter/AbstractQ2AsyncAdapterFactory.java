package com.example.tes24.qqueue_module.asyncadapter;

import com.example.tes24.qqueue_module.http.Q2HttpHeader;

public abstract class AbstractQ2AsyncAdapterFactory implements Q2AsyncAdapterFactory {

    @Override
    public Q2AsyncAdapter getInstance(Q2HttpHeader httpHeaderInfo) {
        return createQ2Adapter(httpHeaderInfo);
    }

    abstract protected Q2AsyncAdapter createQ2Adapter(Q2HttpHeader httpHeaderInfo);
}
