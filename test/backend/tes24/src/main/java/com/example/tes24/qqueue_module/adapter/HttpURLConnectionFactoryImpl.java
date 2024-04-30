package com.example.tes24.qqueue_module.adapter;

import com.example.tes24.qqueue_module.dto.Q2HttpHeader;
import com.example.tes24.qqueue_module.exception.HttpURLConnectionBuildException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class HttpURLConnectionFactoryImpl extends AbstractHttpURLConnectionFactory {
    @Override
    protected HttpURLConnection createHttpURLConnection(Q2HttpHeader q2HttpHeader) {
        if (q2HttpHeader == null) {
            throw new HttpURLConnectionBuildException("Q2HttpHeader is null. Can not build HttpURLConnection.");
        }

        try {
            String url = q2HttpHeader.getUrl();
            Integer timeout = q2HttpHeader.getTimeout();
            String method = q2HttpHeader.getMethod();
            ContentType contentType = q2HttpHeader.getContentType();

            HttpURLConnection urlConnection = (HttpURLConnection) URI.create(url).toURL().openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            if (timeout != null) urlConnection.setConnectTimeout(timeout);
            if (method != null) urlConnection.setRequestMethod(method);
            if (contentType != null) {
                if (contentType.equals(ContentType.JSON)) {
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                } else {
                    urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                }
            }

            return urlConnection;
        } catch (IOException e) {
            throw new HttpURLConnectionBuildException(e);
        }
    }
}
