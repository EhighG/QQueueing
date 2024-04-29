package com.example.tes24.qqueue_module.adapter;

import com.example.tes24.qqueue_module.adapter.Q2Adapter;
import com.example.tes24.qqueue_module.dto.Q2ClientRequest;
import com.example.tes24.qqueue_module.dto.Q2ServerResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Q2AsyncAdapter implements Q2Adapter {
    private byte[] writeBuffer;
    private byte[] readBuffer;
    private final InetSocketAddress clientSocketAddress = new InetSocketAddress(80);
    private final InetSocketAddress serverSocketAddress = new InetSocketAddress("qqueueing", 8080);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Q2ServerResponse enqueue(Q2ClientRequest q2ClientRequest) {
        try {
            send(q2ClientRequest);
            AsynchronousSocketChannel response = receive().get();
            readBuffer = new byte[4096];
            response.read(ByteBuffer.wrap(readBuffer)).get();
            return null;
        } catch (ExecutionException | InterruptedException | IOException e) {
            return null;
        }
    }

    private Future<Integer> send(Q2ClientRequest q2ClientRequest) throws IOException {
        AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
        client.bind(clientSocketAddress);
        client.connect(serverSocketAddress);

        String json = objectMapper.writeValueAsString(q2ClientRequest);
        writeBuffer = json.getBytes(StandardCharsets.UTF_8);
        return client.write(ByteBuffer.wrap(writeBuffer));
    }

    private Future<AsynchronousSocketChannel> receive() throws IOException {
        AsynchronousServerSocketChannel receive = AsynchronousServerSocketChannel.open();
        receive.bind(clientSocketAddress);
        return receive.accept();
    }
}
