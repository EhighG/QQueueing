package com.example.tes24.qqueueing.channel.urlconnection;

import com.example.tes24.qqueueing.channel.ContentType;
import com.example.tes24.qqueueing.channel.Q2HttpHeader;
import com.example.tes24.qqueueing.exception.HttpURLConnectionBuildException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;

public class HttpURLConnectionFactoryImpl extends AbstractHttpURLConnectionFactory {
    @Override
    protected HttpURLConnection createHttpURLConnection(Q2HttpHeader q2HttpHeader) {
        if (q2HttpHeader == null) {
            throw new HttpURLConnectionBuildException("Q2HttpHeader is null. Can not build HttpURLConnection.");
        }

        try {
            String host = q2HttpHeader.getHost();
            Integer port = q2HttpHeader.getPort();
            String path = q2HttpHeader.getPath();
            String method = q2HttpHeader.getMethod();

            String url = new StringBuilder("http://").append(host).append(":").append(port).append(path).toString();
            HttpURLConnection urlConnection = (HttpURLConnection) URI.create(url).toURL().openConnection();
            urlConnection.setRequestMethod(method);
            urlConnection.setRequestProperty("Content-Type", ContentType.JSON.getTypeValue());
            urlConnection.setConnectTimeout(15000);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            return urlConnection;
        } catch (IOException e) {
            throw new HttpURLConnectionBuildException(e);
        }
    }
}
