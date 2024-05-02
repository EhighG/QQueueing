package com.example.tes24.qqueue_module.syncadapter;

import com.example.tes24.qqueue_module.dto.Q2ClientRequest;
import com.example.tes24.qqueue_module.http.ContentType;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URLConnection;

public class Q2SerializeRequester implements Q2Requester {
    @Override
    public void request(Q2ClientRequest q2ClientRequest, URLConnection urlConnection) {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(urlConnection.getOutputStream())) {
            objectOutputStream.writeObject(q2ClientRequest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean support(ContentType contentType) {
        return contentType.equals(ContentType.TEXT_PLAIN);
    }
}
