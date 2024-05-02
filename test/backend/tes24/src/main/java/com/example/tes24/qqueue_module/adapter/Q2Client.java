package com.example.tes24.qqueue_module.adapter;

import com.example.tes24.qqueue_module.dto.Q2ClientRequest;
import com.example.tes24.qqueue_module.dto.Q2HttpHeader;
import com.example.tes24.qqueue_module.dto.Q2ServerResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

public final class Q2Client {
    static {
        instance = new Q2Client();
        reentrantLock = new ReentrantLock();
    }

    private static final Q2Client instance;
    private static final ReentrantLock reentrantLock;

    private final List<? extends Q2SyncAdapterFactory> q2SyncAdapterFactoryList;
    private final List<? extends Q2AsyncAdapterFactory> q2AsyncAdapterFactoryList;
    private final Q2AdapterDelegator delegator;

    private Q2Client() {
        List<Q2SyncAdapterFactory> tempQ2AdapterFactoryList = new ArrayList<>();
        tempQ2AdapterFactoryList.add(new Q2SyncAdapterFactoryImpl(new HttpURLConnectionFactoryImpl()));
        q2SyncAdapterFactoryList = Collections.unmodifiableList(tempQ2AdapterFactoryList);

        List<Q2AsyncAdapterFactory> tempQ2AsyncAdapterFactoryList = new ArrayList<>();
        tempQ2AsyncAdapterFactoryList.add(new Q2AsyncAdapterFactoryImpl(new HttpURLConnectionFactoryImpl()));
        q2AsyncAdapterFactoryList = Collections.unmodifiableList(tempQ2AsyncAdapterFactoryList);

        delegator = new Q2AdapterDelegator(q2SyncAdapterFactoryList, q2AsyncAdapterFactoryList);
    }

    public static Q2Client getQ2Client() {
        reentrantLock.lock();
        try {
            return instance;
        } finally {
            reentrantLock.unlock();
        }
    }

    public Q2ServerResponse request(Q2HttpHeader httpHeader, Q2ClientRequest request) {
        assert httpHeader != null;

        return delegator.delegate(httpHeader, request);
    }

    public Future<Q2ServerResponse> requestAsync(Q2HttpHeader httpHeader, Q2ClientRequest request) {
        assert httpHeader != null;

        return delegator.delegateAsync(httpHeader, request);
    }
}
