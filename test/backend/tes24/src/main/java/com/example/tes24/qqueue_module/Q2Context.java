package com.example.tes24.qqueue_module;

import com.example.tes24.qqueue_module.http.Q2HttpHeaderProperties;
import com.example.tes24.qqueue_module.http.urlconnection.HttpURLConnectionFactoryImpl;
import com.example.tes24.qqueue_module.syncadapter.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Q2Context {
    static {
        Q2ConfigurationLoader.load();

        List<Q2Requester> q2RequesterList = new ArrayList<>();
        q2RequesterList.add(new Q2SerializeRequester());
        q2RequesterList.add(new Q2JsonRequester());
        Q_2_REQUESTERS = Collections.unmodifiableList(q2RequesterList);

        List<Q2SyncAdapterFactory> tempQ2AdapterFactoryList = new ArrayList<>();
        tempQ2AdapterFactoryList.add(new Q2SyncAdapterFactoryImpl(new HttpURLConnectionFactoryImpl()));
        Q_2_SYNC_ADAPTER_FACTORIES = Collections.unmodifiableList(tempQ2AdapterFactoryList);
    }

    private static final Collection<? extends Q2Requester> Q_2_REQUESTERS;

    private static final Collection<? extends Q2SyncAdapterFactory> Q_2_SYNC_ADAPTER_FACTORIES;

    private static Q2HttpHeaderProperties q2HttpHeaderProperties;

    public static Collection<? extends Q2Requester> getQ2Requesters() {
        assert Q_2_REQUESTERS != null && Q_2_REQUESTERS.size() > 0;

        return Q_2_REQUESTERS;
    }

    public static Collection<? extends Q2SyncAdapterFactory> getQ2SyncAdapterFactories() {
        assert Q_2_SYNC_ADAPTER_FACTORIES != null && Q_2_SYNC_ADAPTER_FACTORIES.size() > 0;

        return Q_2_SYNC_ADAPTER_FACTORIES;
    }

    public static void setQ2HttpHeaderProperties(Q2HttpHeaderProperties q2HttpHeaderProperties) {
        Q2Context.q2HttpHeaderProperties = q2HttpHeaderProperties;
    }

    public static Q2HttpHeaderProperties getQ2HttpHeaderProperties() {
        return Q2Context.q2HttpHeaderProperties;
    }

}
