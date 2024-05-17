package com.example.tes24.qqueueing.channel.asyncrohouschannel;

import java.nio.channels.AsynchronousSocketChannel;

public abstract class AbstractAsynchronousChannelFactory implements AsynchronousChannelFactory {
    @Override
    public AsynchronousSocketChannel getInstance() {
        return createAsynchronousChannel();
    }

    abstract protected AsynchronousSocketChannel createAsynchronousChannel();
}
