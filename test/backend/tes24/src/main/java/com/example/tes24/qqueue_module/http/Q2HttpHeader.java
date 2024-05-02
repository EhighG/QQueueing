package com.example.tes24.qqueue_module.http;

import com.example.tes24.qqueue_module.Q2Context;
import com.example.tes24.qqueue_module.http.urlconnection.HttpURLConnectionFactory;

import java.net.HttpURLConnection;
import java.net.http.HttpHeaders;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The class which has HTTP header's information to performing HTTP connection correctly between The QQueuing api server.
 * You can customize uri to request, timeout, method and content type.
 *
 * <p>The {@link HttpURLConnectionFactory} constructs {@link HttpURLConnection} instance base on this class.
 *
 * @since 1.0
 * @author Moon-Young Shin
 */
public class Q2HttpHeader {
    private String url;
    private String contentType;
    private Integer timeout;
    private String method;

    private Q2HttpHeader() {}

    private Q2HttpHeader(Q2HttpHeaderProperties q2HttpHeaderProperties) {
        this.url = q2HttpHeaderProperties.getUrl();
        this.contentType = q2HttpHeaderProperties.getContentType();
        this.timeout = q2HttpHeaderProperties.getTimeout();
        this.method = q2HttpHeaderProperties.getMethod();
    }

    public static Q2HttpHeader defaultQ2HttpHeader() {
        return new Q2HttpHeader(Q2Context.getQ2HttpHeaderProperties());
    }

    public static Q2HttpHeader withProperties(Q2HttpHeaderProperties q2HttpHeaderProperties) {
        assert q2HttpHeaderProperties != null;

        return new Q2HttpHeader(q2HttpHeaderProperties);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Q2HttpHeader that = (Q2HttpHeader) object;
        return Objects.equals(url, that.url) && Objects.equals(contentType, that.contentType) && Objects.equals(timeout, that.timeout) && Objects.equals(method, that.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, contentType, timeout, method);
    }

    @Override
    public String toString() {
        return "Q2HttpHeader{" +
                "url='" + url + '\'' +
                ", contentType=" + contentType +
                ", timeout=" + timeout +
                ", method='" + method + '\'' +
                '}';
    }

    public static Q2HttpHeaderBuilder builder() {
        return new Q2HttpHeaderBuilder();
    }

    public static class Q2HttpHeaderBuilder {
        private final Q2HttpHeader q2HttpHeader;
        private Q2HttpHeaderBuilder() {
            this.q2HttpHeader = new Q2HttpHeader();
        }

        public Q2HttpHeaderBuilder url(String url) {
            this.q2HttpHeader.url = url;
            return this;
        }

        public Q2HttpHeaderBuilder contentType(String contentType) {
            this.q2HttpHeader.contentType = contentType;
            return this;
        }

        public Q2HttpHeaderBuilder timeout(Integer timeout) {
            this.q2HttpHeader.timeout = timeout;
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

    public String getUrl() {
        return url;
    }

    public String getContentType() {
        return contentType;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public String getMethod() {
        return method;
    }

}
