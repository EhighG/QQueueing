package com.example.tes24.qqueueing.channel;

import com.example.tes24.qqueueing.channel.urlconnection.HttpURLConnectionFactory;

import java.net.HttpURLConnection;

/**
 * The class which has HTTP header's information to performing HTTP connection correctly between The QQueuing api server.
 * You can customize uri to request and method.
 *
 * <p>The {@link HttpURLConnectionFactory} constructs {@link HttpURLConnection} instance base on this class.
 *
 * @since 1.1
 * @author Moon-Young Shin
 */
public class Q2HttpHeader {
    private String host;
    private Integer port;
    private String path;
    private String method;

    public static Q2HttpHeaderBuilder builder() {
        return new Q2HttpHeaderBuilder();
    }

    public static class Q2HttpHeaderBuilder {
        private final Q2HttpHeader q2HttpHeader;
        private Q2HttpHeaderBuilder() {
            this.q2HttpHeader = new Q2HttpHeader();
        }

        public Q2HttpHeaderBuilder host(String host) {
            this.q2HttpHeader.host = host;
            return this;
        }

        public Q2HttpHeaderBuilder port(Integer port) {
            this.q2HttpHeader.port = port;
            return this;
        }

        public Q2HttpHeaderBuilder path(String path) {
            this.q2HttpHeader.path = path;
            return this;
        }

        public Q2HttpHeaderBuilder method(String method) {
            this.q2HttpHeader.method = method;
            return this;
        }

        public Q2HttpHeader build() {
            return this.q2HttpHeader;
        }
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }

}
