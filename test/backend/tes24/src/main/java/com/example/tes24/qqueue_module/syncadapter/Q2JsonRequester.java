package com.example.tes24.qqueue_module.syncadapter;

import com.example.tes24.qqueue_module.dto.Q2ClientRequest;
import com.example.tes24.qqueue_module.http.ContentType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URLConnection;

public class Q2JsonRequester implements Q2Requester {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void request(Q2ClientRequest q2ClientRequest, URLConnection urlConnection) {
        try {
            String json = this.objectMapper.writeValueAsString(q2ClientRequest);
            try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()))) {
                bufferedWriter.write(json);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean support(ContentType contentType) {
        return contentType.equals(ContentType.JSON);
    }
}
