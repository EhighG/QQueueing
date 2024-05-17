package com.example.tes24.qqueueing.configuration;


import com.example.tes24.qqueueing.annotation.Q2AutoConfiguration;
import com.example.tes24.qqueueing.annotation.Q2Bean;
import com.example.tes24.qqueueing.channel.Q2HttpHeader;
import com.example.tes24.qqueueing.channel.urlconnection.HttpURLConnectionFactory;
import com.example.tes24.qqueueing.channel.urlconnection.HttpURLConnectionFactoryImpl;
import com.example.tes24.qqueueing.adapter.sync.Q2SyncAdapter;
import com.example.tes24.qqueueing.adapter.sync.Q2SyncAdapterFactory;
import com.example.tes24.qqueueing.adapter.sync.Q2SyncAdapterFactoryImpl;

@Q2AutoConfiguration
public class Q2SyncAdapterConfiguration {
    @Q2Bean
    public HttpURLConnectionFactory httpURLConnectionFactory() {
        return new HttpURLConnectionFactoryImpl();
    }

    @Q2Bean
    public Q2SyncAdapterFactory q2SyncAdapterFactory(HttpURLConnectionFactory httpURLConnectionFactory) {
        return new Q2SyncAdapterFactoryImpl(httpURLConnectionFactory);
    }

    @Q2Bean
    public Q2SyncAdapter q2SyncAdapter(Q2SyncAdapterFactory q2SyncAdapterFactory, Q2HttpHeader q2HttpHeader) {
        return q2SyncAdapterFactory.getInstance(q2HttpHeader);
    }
}
