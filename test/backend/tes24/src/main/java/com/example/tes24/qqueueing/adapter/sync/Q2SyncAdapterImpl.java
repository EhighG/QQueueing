package com.example.tes24.qqueueing.adapter.sync;

import com.example.tes24.qqueueing.dto.Q2ClientRequest;
import com.example.tes24.qqueueing.dto.Q2ServerResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.*;
import java.util.Optional;

/**
 * The default implementation of {@link Q2SyncAdapter}.
 *
 * @since 1.0
 * @author Moon-Young Shin
 */
public class Q2SyncAdapterImpl implements Q2SyncAdapter {
    private final HttpURLConnection urlConnection;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Q2SyncAdapterImpl(HttpURLConnection urlConnection) {
        this.urlConnection = urlConnection;
    }

    @Override
    public Q2ServerResponse request(Q2ClientRequest q2ClientRequest) {
        try {
            request(q2ClientRequest, urlConnection);
            return response(urlConnection).orElseThrow(() -> new IOException("No response."));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            urlConnection.disconnect();
        }
    }

    private void request(Q2ClientRequest q2ClientRequest, URLConnection urlConnection) {
        try {
            String json = this.objectMapper.writeValueAsString(q2ClientRequest);
            try (OutputStream outputStream = urlConnection.getOutputStream()) {
                try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream))) {
                    bufferedWriter.write(json);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<Q2ServerResponse> response(URLConnection urlConnection) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            try (InputStream inputStream = urlConnection.getInputStream()) {
                try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
                    try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                        stringBuilder.append(bufferedReader.readLine());
                        return Optional.of(this.objectMapper.readValue(stringBuilder.toString(), Q2ServerResponse.class));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return Optional.empty();
        }
    }
}
