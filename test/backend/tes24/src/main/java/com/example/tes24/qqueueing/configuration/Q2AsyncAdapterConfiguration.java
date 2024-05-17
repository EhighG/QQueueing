package com.example.tes24.qqueueing.configuration;

import com.example.tes24.qqueueing.annotation.Q2AutoConfiguration;
import com.example.tes24.qqueueing.annotation.Q2Bean;
import com.example.tes24.qqueueing.adapter.async.Q2AsyncAdapter;
import com.example.tes24.qqueueing.adapter.async.Q2AsyncAdapterFactory;
import com.example.tes24.qqueueing.adapter.async.Q2AsyncAdapterFactoryImpl;
import com.example.tes24.qqueueing.channel.Q2HttpHeader;
import com.example.tes24.qqueueing.channel.asyncrohouschannel.AsynchronousChannelFactory;
import com.example.tes24.qqueueing.channel.asyncrohouschannel.AsynchronousChannelFactoryImpl;

@Q2AutoConfiguration
public class Q2AsyncAdapterConfiguration {
    @Q2Bean
    public AsynchronousChannelFactory asynchronousChannelFactory() {
        return new AsynchronousChannelFactoryImpl();
    }

    @Q2Bean
    public Q2AsyncAdapterFactory q2AsyncAdapterFactory(AsynchronousChannelFactory asynchronousChannelFactory) {
        return new Q2AsyncAdapterFactoryImpl(asynchronousChannelFactory);
    }

    @Q2Bean
    public Q2AsyncAdapter q2AsyncAdapter(Q2AsyncAdapterFactory q2AsyncAdapterFactory, Q2HttpHeader q2HttpHeader) {
        return q2AsyncAdapterFactory.getInstance(q2HttpHeader);
    }
}
