package com.example.tes24.qqueue_module;

import com.example.tes24.qqueue_module.http.urlconnection.HttpURLConnectionFactoryImpl;
import com.example.tes24.qqueue_module.syncadapter.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Q2Context {
    static {
        List<Q2Requester> q2RequesterList = new ArrayList<>();
        q2RequesterList.add(new Q2SerializeRequester());
        q2RequesterList.add(new Q2JsonRequester());
        q2Requesters = Collections.unmodifiableList(q2RequesterList);

        List<Q2SyncAdapterFactory> tempQ2AdapterFactoryList = new ArrayList<>();
        tempQ2AdapterFactoryList.add(new Q2SyncAdapterFactoryImpl(new HttpURLConnectionFactoryImpl()));

        q2SyncAdapterFactories = Collections.unmodifiableList(tempQ2AdapterFactoryList);
    }

    private static final Collection<? extends Q2Requester> q2Requesters;

    private static final Collection<? extends Q2SyncAdapterFactory> q2SyncAdapterFactories;

    public static Collection<? extends Q2Requester> getQ2Requesters() {
        assert q2Requesters != null && q2Requesters.size() > 0;

        return q2Requesters;
    }

    public static Collection<? extends Q2SyncAdapterFactory> getQ2SyncAdapterFactories() {
        assert q2SyncAdapterFactories != null && q2SyncAdapterFactories.size() > 0;

        return q2SyncAdapterFactories;
    }

}
