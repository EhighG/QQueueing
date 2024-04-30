package com.example.tes24.qqueue_module.adapter;

import com.example.tes24.qqueue_module.dto.Q2ClientRequest;
import com.example.tes24.qqueue_module.dto.Q2ServerResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.*;
import java.util.Optional;

public class Q2SyncAdapterImpl implements Q2SyncAdapter {
    private final HttpURLConnection urlConnection;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Q2SyncAdapterImpl(HttpURLConnection urlConnection) {
        assert urlConnection != null;

        this.urlConnection = urlConnection;
    }

    @Override
    public Q2ServerResponse enqueue(Q2ClientRequest q2ClientRequest) {
        try {
            urlConnection.connect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Optional<Object> response = enqueue(q2ClientRequest, urlConnection);

        urlConnection.disconnect();

        try {
            if (response.orElseThrow(IOException::new) instanceof Q2ServerResponse qqResponse) {
                return qqResponse;
            } else {
                throw new ClassCastException();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<Object> enqueue(Q2ClientRequest q2ClientRequest, URLConnection urlConnection) {
        String contentType = urlConnection.getRequestProperty("Content-Type");
        return contentType != null && ContentType.JSON.getTypeValue().equals(contentType) ?
                enqueueWithJson(q2ClientRequest, urlConnection) :
                enqueueWithSerialization(q2ClientRequest, urlConnection);
    }

    private Optional<Object> enqueueWithSerialization(Q2ClientRequest q2ClientRequest, URLConnection urlConnection) {
        try {
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(urlConnection.getOutputStream())) {
                objectOutputStream.writeObject(q2ClientRequest);
            }

            try (ObjectInputStream objectInputStream = new ObjectInputStream(urlConnection.getInputStream())) {
                return Optional.of(objectInputStream.readObject());
            }
        } catch (IOException | ClassNotFoundException e) {
            return Optional.empty();
        }
    }

    private Optional<Object> enqueueWithJson(Q2ClientRequest q2ClientRequest, URLConnection urlConnection) {
        try {
            String json = this.objectMapper.writeValueAsString(q2ClientRequest);

            try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()))) {
                bufferedWriter.write(json);
            }

            StringBuilder stringBuilder = new StringBuilder();
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
                stringBuilder.append(bufferedReader.readLine());
            }

            return Optional.of(this.objectMapper.readValue(stringBuilder.toString(), Q2ServerResponse.class));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

}
