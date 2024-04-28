package com.example.tes24.qqueue_module.adapter;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;

public class Q2AdapterBuilder {
    private static final int DEFAULT_TIMEOUT = 15000;
    private static final String DEFAULT_METHOD = "POST";
    private static final Mode DEFAULT_MODE = Mode.JSON;
    private String url;
    private Mode mode = DEFAULT_MODE;
    private Integer timeout = DEFAULT_TIMEOUT;
    private String method = DEFAULT_METHOD;

    public static Q2AdapterBuilder builder() {
        return new Q2AdapterBuilder();
    }

    public Q2AdapterBuilder url(String url) {
        this.url = url;
        return this;
    }

    public Q2AdapterBuilder timeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public Q2AdapterBuilder method(String method) {
        this.method = method;
        return this;
    }

    public Q2AdapterBuilder mode(Mode mode) {
        this.mode = mode;
        return this;
    }

    public Q2Adapter build() {
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) URI.create(this.url).toURL().openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            if (timeout != null) urlConnection.setConnectTimeout(timeout);
            if (method != null) urlConnection.setRequestMethod(method);
            if (mode != null) {
                if (mode.equals(Mode.JSON)) {
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                }
            }
            return new Q2Adapter(urlConnection);
        } catch (IOException e) {
            return null;
        }
    }
}
