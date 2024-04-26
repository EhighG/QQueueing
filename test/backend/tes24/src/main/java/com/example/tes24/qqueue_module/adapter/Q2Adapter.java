package com.example.tes24.qqueue_module.adapter;

import com.example.tes24.qqueue_module.dto.Q2ClientRequest;
import com.example.tes24.qqueue_module.dto.Q2ServerResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.*;
import java.util.Optional;

public class Q2Adapter {
    private static final String DEFAULT_METHOD = "POST";
    private static final int DEFAULT_TIMEOUT = 15000;
    private static final Mode MODE = Mode.JSON;
    private final String qqServerUrl;
    private final ObjectMapper objectMapper = new ObjectMapper();

    static Q2AdapterBuilder q2AdapterBuilder;

    public Q2Adapter(String qqServerUrl) {
        assert qqServerUrl != null && !qqServerUrl.isEmpty();

        this.qqServerUrl = qqServerUrl;
    }

    static Q2AdapterBuilder builder() {
        if (q2AdapterBuilder == null) {
            q2AdapterBuilder = new Q2AdapterBuilder();
        }

        return q2AdapterBuilder;
    }

    private URL getURL() throws MalformedURLException {
        return URI.create(this.qqServerUrl).toURL();
    }

    private HttpURLConnection getHttpConnection() throws IOException {
        URL url = getURL();
        return (HttpURLConnection) url.openConnection();
    }

    public Q2ServerResponse enqueue(Q2ClientRequest q2ClientRequest) throws IOException {
        HttpURLConnection urlConnection = getHttpConnection();
        urlConnection.setConnectTimeout(DEFAULT_TIMEOUT);
        urlConnection.setRequestMethod(DEFAULT_METHOD);
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        urlConnection.setRequestProperty("Content-Type", "application/json");

        urlConnection.connect();

        Optional<Object> response = enqueue(q2ClientRequest, urlConnection);

        urlConnection.disconnect();

        if (response.orElseThrow(IOException::new) instanceof Q2ServerResponse qqResponse) {
            return qqResponse;
        } else {
            throw new ClassCastException();
        }
    }

    private Optional<Object> enqueue(Q2ClientRequest q2ClientRequest, URLConnection urlConnection) {
        return MODE.equals(Mode.JSON) ?
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
        assert objectMapper != null;

        try {
            String json = this.objectMapper.writeValueAsString(q2ClientRequest);

            try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()))) {
                bufferedWriter.write(json);
            }

            StringBuilder stringBuilder = new StringBuilder();
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
                stringBuilder.append(bufferedReader.readLine());
            }

            return Optional.of(this.objectMapper.readValue(stringBuilder.toString(), Object.class));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    static class Q2AdapterBuilder {
        private String url;

        static Q2AdapterBuilder builder() {
            return new Q2AdapterBuilder();
        }

        public Q2AdapterBuilder url(String url) {
            this.url = url;
            return this;
        }

        public Q2Adapter build() {

            Q2Adapter q2Adapter = new Q2Adapter(this.url);

            return q2Adapter;
        }
    }

//    static class Q2

    private enum Mode {
        SERIALIZE, JSON
    }

}
