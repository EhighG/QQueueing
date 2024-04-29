package com.example.tes24.qqueue_module.adapter;

import com.example.tes24.qqueue_module.dto.Q2ClientRequest;
import com.example.tes24.qqueue_module.dto.Q2ServerResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

public class Q2AdapterDelegator {
    private static final List<Q2AdapterFactory> q2AdapterFactoryList;

    static {
        List<Q2AdapterFactory> temp = new ArrayList<>();
        temp.add(new Q2SyncAdapterFactory());
        temp.add(new Q2AsyncAdapterFactory());
        q2AdapterFactoryList = Collections.unmodifiableList(temp);
    }

    public Q2ServerResponse enqueue(Q2ClientRequest q2ClientRequest, Class<Q2Adapter> delegateClass) {
        return q2AdapterFactoryList.stream()
                .filter(q2AdapterFactory ->  q2AdapterFactory.getClass().equals(delegateClass))
                .map(q2AdapterFactory -> q2AdapterFactory.getInstance(null))
                .map(q2Adapter -> q2Adapter.enqueue(q2ClientRequest))
                .findFirst().orElseThrow(NoSuchElementException::new);
    }
}
