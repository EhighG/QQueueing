package com.example.tes24.qqueueing.channel.asyncrohouschannel;

import com.example.tes24.qqueueing.exception.AsynchronousChannelBuildException;

import java.nio.channels.AsynchronousSocketChannel;

public class AsynchronousChannelFactoryImpl extends  AbstractAsynchronousChannelFactory {
    @Override
    protected AsynchronousSocketChannel createAsynchronousChannel() {
        try {
            return AsynchronousSocketChannel.open();
        } catch (Exception e) {
            throw new AsynchronousChannelBuildException(e);
        }
    }
}
