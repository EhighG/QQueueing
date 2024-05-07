package com.example.tes24.qqueue_module;

import com.example.tes24.qqueue_module.http.Q2HttpHeaderProperties;
import com.example.tes24.qqueue_module.http.urlconnection.HttpURLConnectionFactoryImpl;
import com.example.tes24.qqueue_module.syncadapter.*;

import java.util.*;

public class Q2Context {
    private Q2Context() {}

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

    private static Q2HttpHeaderProperties defaultHeaderProperties;
    private static Q2HttpHeaderProperties monitorHeaderProperties;

    public static Collection<? extends Q2Requester> getQ2Requesters() {
        assert Q_2_REQUESTERS != null && Q_2_REQUESTERS.size() > 0;

        return Q_2_REQUESTERS;
    }

    public static Collection<? extends Q2SyncAdapterFactory> getQ2SyncAdapterFactories() {
        assert Q_2_SYNC_ADAPTER_FACTORIES != null && Q_2_SYNC_ADAPTER_FACTORIES.size() > 0;

        return Q_2_SYNC_ADAPTER_FACTORIES;
    }

    public static void setDefaultHeader(Q2HttpHeaderProperties q2HttpHeaderProperties) {
        Q2Context.defaultHeaderProperties = q2HttpHeaderProperties;
    }

    public static Q2HttpHeaderProperties getDefaultHeaderProperties() {
        return Q2Context.defaultHeaderProperties;
    }

    public static void setMonitorHeader(Q2HttpHeaderProperties q2HttpHeaderProperties) {
        Q2Context.monitorHeaderProperties = q2HttpHeaderProperties;
    }

    public static Q2HttpHeaderProperties getMonitorHeaderProperties() { return Q2Context.monitorHeaderProperties; }

}
