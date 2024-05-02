package com.example.tes24.qqueue_module;

import com.example.tes24.qqueue_module.syncadapter.Q2SyncAdapterFactory;
import com.example.tes24.qqueue_module.syncadapter.Q2SyncAdapterFactoryImpl;
import com.example.tes24.qqueue_module.dto.Q2ClientRequest;
import com.example.tes24.qqueue_module.http.Q2HttpHeader;
import com.example.tes24.qqueue_module.dto.Q2ServerResponse;

import java.util.*;
import java.util.stream.Collectors;

public class Q2AdapterDelegator {
    private static final Class<? extends Q2SyncAdapterFactory> DEFAULT_Q2_SYNC_ADAPTER_FACTORY_CLASS = Q2SyncAdapterFactoryImpl.class;
    private final List<? extends Q2SyncAdapterFactory> q2SyncAdapterFactoryList;

    public Q2AdapterDelegator(Collection<? extends Q2SyncAdapterFactory> q2AdapterFactories) {
        this.q2SyncAdapterFactoryList = q2AdapterFactories.stream().collect(Collectors.toUnmodifiableList());
    }

    public Q2ServerResponse delegate(Q2HttpHeader httpHeader, Q2ClientRequest q2ClientRequest) {
        return delegate(httpHeader, q2ClientRequest, DEFAULT_Q2_SYNC_ADAPTER_FACTORY_CLASS);
    }

    public Q2ServerResponse delegate(Q2HttpHeader httpHeader, Q2ClientRequest q2ClientRequest, Class<? extends Q2SyncAdapterFactory> q2SyncAdapterFactoryClass) {
        return q2SyncAdapterFactoryList.stream()
                .filter(q2AdapterFactory ->  q2AdapterFactory.getClass().equals(q2SyncAdapterFactoryClass))
                .map(q2AdapterFactory -> q2AdapterFactory.getInstance(httpHeader))
                .map(q2Adapter -> q2Adapter.request(q2ClientRequest))
                .findFirst().orElseThrow(NoSuchElementException::new);
    }
}
