package com.example.tes24.qqueueing.channel.asyncrohouschannel;

import java.nio.channels.AsynchronousSocketChannel;

public interface AsynchronousChannelFactory {
    AsynchronousSocketChannel getInstance();
}
