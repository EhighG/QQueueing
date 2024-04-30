package com.example.tes24.qqueue_module.adapter;

import com.example.tes24.qqueue_module.dto.Q2ClientRequest;
import com.example.tes24.qqueue_module.dto.Q2HttpHeader;
import com.example.tes24.qqueue_module.dto.Q2ServerResponse;

import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class Q2AdapterDelegator {
    private static final Class<? extends Q2SyncAdapterFactory> DEFAULT_Q2_SYNC_ADAPTER_FACTORY_CLASS = Q2SyncAdapterFactory.class;
    private static final Class<? extends Q2AsyncAdapterFactory> DEFAULT_Q2_ASYNC_ADAPTER_FACTORY_CLASS = Q2AsyncAdapterFactory.class;
    private final List<? extends Q2SyncAdapterFactory> q2SyncAdapterFactoryList;
    private final List<? extends Q2AsyncAdapterFactory> q2AsyncAdapterFactoryList;

    public Q2AdapterDelegator(Collection<? extends Q2SyncAdapterFactory> q2AdapterFactories, Collection<? extends Q2AsyncAdapterFactory> q2AsyncAdapterFactories) {
        this.q2SyncAdapterFactoryList = q2AdapterFactories.stream().collect(Collectors.toUnmodifiableList());
        this.q2AsyncAdapterFactoryList = q2AsyncAdapterFactories.stream().collect(Collectors.toUnmodifiableList());
    }

    public Q2ServerResponse delegate(Q2HttpHeader httpHeader, Q2ClientRequest q2ClientRequest) {
        return delegate(httpHeader, q2ClientRequest, DEFAULT_Q2_SYNC_ADAPTER_FACTORY_CLASS);
    }

    public Future<Q2ServerResponse> delegateAsync(Q2HttpHeader httpHeader, Q2ClientRequest q2ClientRequest) {
        return delegateAsync(httpHeader, q2ClientRequest, DEFAULT_Q2_ASYNC_ADAPTER_FACTORY_CLASS);
    }

    public Q2ServerResponse delegate(Q2HttpHeader httpHeader, Q2ClientRequest q2ClientRequest, Class<? extends Q2SyncAdapterFactory> q2SyncAdapterFactoryClass) {
        return q2SyncAdapterFactoryList.stream()
                .filter(q2AdapterFactory ->  q2AdapterFactory instanceof Q2SyncAdapterFactory)
                .map(q2AdapterFactory -> q2AdapterFactory.getInstance(httpHeader))
                .map(q2Adapter -> q2Adapter.enqueue(q2ClientRequest))
                .findFirst().orElseThrow(NoSuchElementException::new);
    }

    public Future<Q2ServerResponse> delegateAsync(Q2HttpHeader httpHeader, Q2ClientRequest q2ClientRequest, Class<? extends Q2AsyncAdapterFactory> q2AsyncAdapterFactoryClass) {
        return q2AsyncAdapterFactoryList.stream()
                .filter(q2AdapterFactory ->  q2AdapterFactory.getClass().equals(q2AsyncAdapterFactoryClass))
                .map(q2AdapterFactory -> q2AdapterFactory.getInstance(httpHeader))
                .map(q2Adapter -> q2Adapter.enqueue(q2ClientRequest))
                .findFirst().orElseThrow(NoSuchElementException::new);
    }
}
