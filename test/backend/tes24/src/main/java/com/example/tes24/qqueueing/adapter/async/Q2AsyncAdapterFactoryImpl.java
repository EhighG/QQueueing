package com.example.tes24.qqueueing.adapter.async;

import com.example.tes24.qqueueing.channel.Q2HttpHeader;
import com.example.tes24.qqueueing.channel.asyncrohouschannel.AsynchronousChannelFactory;

public class Q2AsyncAdapterFactoryImpl extends AbstractQ2AsyncAdapterFactory {
    private final AsynchronousChannelFactory asynchronousChannelFactory;

    public Q2AsyncAdapterFactoryImpl(AsynchronousChannelFactory asynchronousChannelFactory) {
        this.asynchronousChannelFactory = asynchronousChannelFactory;
    }
    @Override
    protected Q2AsyncAdapter createQ2Adapter(Q2HttpHeader q2HttpHeader) {
        return new Q2AsyncAdapterImpl(asynchronousChannelFactory.getInstance(), q2HttpHeader);
    }
}
