package com.example.tes24.qqueue_module.syncadapter;

import com.example.tes24.qqueue_module.dto.Q2ClientRequest;
import com.example.tes24.qqueue_module.dto.Q2ServerResponse;
import com.example.tes24.qqueue_module.http.ContentType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.*;
import java.util.Collection;
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
    private final Collection<? extends Q2Requester> q2Requesters;

    public Q2SyncAdapterImpl(HttpURLConnection urlConnection, Collection<? extends Q2Requester> q2Requesters) {
        this.urlConnection = urlConnection;
        this.q2Requesters = q2Requesters;
    }

    @Override
    public Q2ServerResponse request(Q2ClientRequest q2ClientRequest) {
        try {
            urlConnection.connect();
            Optional<Object> response = request(q2ClientRequest, urlConnection);
            urlConnection.disconnect();

            if (response.orElseThrow(IOException::new) instanceof Q2ServerResponse qqResponse) {
                return qqResponse;
            } else {
                throw new ClassCastException("The response is not Q2ServerResponse type.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Optional<Object> request(Q2ClientRequest q2ClientRequest, URLConnection urlConnection) {
        String contentType = urlConnection.getRequestProperty("Content-Type");
        q2Requesters.stream()
                .filter(q2Requester -> q2Requester.support(contentType))
                .forEach(q2Requester -> q2Requester.request(q2ClientRequest, urlConnection));
        return response(urlConnection);
    }

    private Optional<Object> response(URLConnection urlConnection) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
                stringBuilder.append(bufferedReader.readLine());
            }
            return Optional.of(this.objectMapper.readValue(stringBuilder.toString(), Q2ServerResponse.class));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return Optional.empty();
        }
    }
}
