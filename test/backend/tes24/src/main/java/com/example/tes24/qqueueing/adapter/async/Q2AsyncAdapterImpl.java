package com.example.tes24.qqueueing.adapter.async;

import com.example.tes24.qqueueing.dto.Q2ClientRequest;
import com.example.tes24.qqueueing.dto.Q2ServerResponse;
import com.example.tes24.qqueueing.exception.RequestException;
import com.example.tes24.qqueueing.channel.Q2HttpHeader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

public class Q2AsyncAdapterImpl implements Q2AsyncAdapter {
    private final AsynchronousSocketChannel asynchronousSocketChannel;
    private final Q2HttpHeader q2HttpHeader;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private ByteBuffer readBuffer;

    public Q2AsyncAdapterImpl(AsynchronousSocketChannel asynchronousSocketChannel, Q2HttpHeader q2HttpHeader) {
        this.asynchronousSocketChannel = asynchronousSocketChannel;
        this.q2HttpHeader = q2HttpHeader;
    }

    @Override
    public CompletableFuture<Q2ServerResponse> requestAsync(Q2ClientRequest q2ClientRequest) {
        try {
            return request(q2ClientRequest).thenApplyAsync(ignore -> response()).get();
        } catch (ExecutionException | InterruptedException | IOException e) {
            throw new RequestException(e);
        }
    }

    private CompletableFuture<Integer> request(Q2ClientRequest q2ClientRequest) throws JsonProcessingException, ExecutionException, InterruptedException {
        CompletableFuture<Integer> completableFuture = new CompletableFuture<>();
        asynchronousSocketChannel.connect(new InetSocketAddress(q2HttpHeader.getHost(), q2HttpHeader.getPort())).get();
        asynchronousSocketChannel.write(createByteBuffer(q2HttpHeader, q2ClientRequest), null,
                new CompletionHandler<>() {
                    @Override
                    public void completed(Integer result, Object attachment) {
                        completableFuture.complete(result);
                    }

                    @Override
                    public void failed(Throwable exc, Object attachment) {
                        completableFuture.completeExceptionally(exc);
                    }
                });

        return completableFuture;
    }

    private ByteBuffer createByteBuffer(Q2HttpHeader q2HttpHeader, Q2ClientRequest q2ClientRequest) throws JsonProcessingException {
        String host = q2HttpHeader.getHost();
        Integer port = q2HttpHeader.getPort();
        String path = q2HttpHeader.getPath();
        String method = q2HttpHeader.getMethod();
        String json = objectMapper.writeValueAsString(q2ClientRequest);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(method).append(" ").append(path).append(" ").append("HTTP/1.1").append("\r\n");
        stringBuilder.append("Host: ").append(host).append(":").append(port).append("\r\n");
        stringBuilder.append("Content-Type: ").append("application/json").append("\r\n");
        stringBuilder.append("Content-Length: ").append(json.length()).append("\r\n");
        stringBuilder.append("\r\n");
        stringBuilder.append(json);

        return ByteBuffer.wrap(stringBuilder.toString().getBytes(StandardCharsets.UTF_8));
    }

    private CompletableFuture<Q2ServerResponse> response() {
        CompletableFuture<Q2ServerResponse> completedFuture = new CompletableFuture<>();
        readBuffer = ByteBuffer.allocate(4096);
        asynchronousSocketChannel.read(readBuffer, null,
                new CompletionHandler<>() {
                    @Override
                    public void completed(Integer result, Object attachment) {
                        readBuffer.flip();
                        byte[] bytes = new byte[readBuffer.remaining()];
                        readBuffer.get(bytes);

                        String[] httpResponse =  new String(bytes, StandardCharsets.UTF_8).split("\r\n\r\n");
                        String[] chunkedResponse = httpResponse[1].split("\r\n");

                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0; i < chunkedResponse.length; i++) {
                            if (i % 2 == 1) {
                                stringBuilder.append(chunkedResponse[i]);
                            }
                        }

                        try {
                            completedFuture.complete(objectMapper.readValue(stringBuilder.toString(), Q2ServerResponse.class));
                        } catch (JsonProcessingException e) {
                            completedFuture.completeExceptionally(e);
                        } finally {
                            try {
                                asynchronousSocketChannel.close();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    @Override
                    public void failed(Throwable exc, Object attachment) {
                        completedFuture.completeExceptionally(exc);
                        try {
                            asynchronousSocketChannel.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

        return completedFuture;
    }
}
