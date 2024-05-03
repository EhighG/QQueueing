package com.example.tes24.qqueue_module.asyncadapter;

import com.example.tes24.qqueue_module.dto.Q2ClientRequest;
import com.example.tes24.qqueue_module.dto.Q2ServerResponse;
import com.example.tes24.qqueue_module.http.Q2HttpHeader;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Q2AsyncAdapterImpl implements Q2AsyncAdapter {
    private final Q2HttpHeader q2HttpHeader;
    private byte[] writeBuffer;
    private byte[] readBuffer;
    private final InetSocketAddress clientSocketAddress = new InetSocketAddress(55555);
    private final InetSocketAddress serverSocketAddress = new InetSocketAddress("localhost", 8080);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Q2AsyncAdapterImpl(Q2HttpHeader q2HttpHeader) {
        this.q2HttpHeader = q2HttpHeader;
    }

    @Override
    public Future<Q2ServerResponse> enqueue(Q2ClientRequest q2ClientRequest) {
        try {
            System.out.println(send(q2ClientRequest).get());
            AsynchronousSocketChannel response = receive().get();
            readBuffer = new byte[4096];
            response.read(ByteBuffer.wrap(readBuffer)).get();
            System.out.println(Arrays.toString(readBuffer));
            return null;
        } catch (ExecutionException | InterruptedException | IOException e) {
            return null;
        }
    }

    private Future<Integer> send(Q2ClientRequest q2ClientRequest) throws IOException {
        AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
        client.bind(clientSocketAddress);
        client.connect(serverSocketAddress);

        String header = "POST /tes24 HTTP/1.1\r\n";
        String type = "Content-Type: application/json\r\n";
        String json = objectMapper.writeValueAsString(q2ClientRequest);

        return client.write(ByteBuffer.wrap(new StringBuilder(header).append(type).append(json).toString().getBytes(StandardCharsets.UTF_8)));
    }

    private Future<AsynchronousSocketChannel> receive() throws IOException {
        AsynchronousServerSocketChannel receive = AsynchronousServerSocketChannel.open();
        receive.bind(clientSocketAddress);
        return receive.accept();
    }
}
