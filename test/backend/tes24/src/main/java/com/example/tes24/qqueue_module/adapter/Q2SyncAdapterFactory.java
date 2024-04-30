package com.example.tes24.qqueue_module.adapter;


import com.example.tes24.qqueue_module.exception.BuildException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;

public class Q2SyncAdapterFactory extends AbstractQ2AdapterFactory {
    @Override
    protected Q2Adapter createQ2Adapter(HttpHeaderInfo httpHeaderInfo) {
        if (httpHeaderInfo == null) {
            return Q2SyncAdapterBuilder.defaultBuild();
        } else {
            return Q2SyncAdapterBuilder.builder(httpHeaderInfo).build();
        }
    }

    static class Q2SyncAdapterBuilder extends AbstractQ2AdapterBuilder {
        private Q2SyncAdapterBuilder() {
            super.httpHeaderInfo = HttpHeaderInfo.defaultHttpHeaderInfo();
        }

        public static Q2Adapter defaultBuild() {
            return new Q2SyncAdapterBuilder().build();
        }

        private Q2SyncAdapterBuilder(HttpHeaderInfo httpHeaderInfo) {
            super.httpHeaderInfo = httpHeaderInfo;
        }

        public static Q2SyncAdapterBuilder builder(HttpHeaderInfo httpHeaderInfo) {
            return new Q2SyncAdapterBuilder(httpHeaderInfo);
        }

        @Override
        public AbstractQ2AdapterBuilder url(String url) {
            super.httpHeaderInfo.setUrl(url);
            return this;
        }

        @Override
        public AbstractQ2AdapterBuilder timeout(int timeout) {
            httpHeaderInfo.setTimeout(timeout);
            return this;
        }

        @Override
        public AbstractQ2AdapterBuilder method(String method) {
            httpHeaderInfo.setMethod(method);
            return this;
        }

        @Override
        public AbstractQ2AdapterBuilder contentType(ContentType contentType) {
            httpHeaderInfo.setContentType(contentType);
            return this;
        }

        @Override
        public Q2Adapter build() {
            try {
                String url = httpHeaderInfo.getUrl();
                Integer timeout = httpHeaderInfo.getTimeout();
                String method = httpHeaderInfo.getMethod();
                ContentType contentType = httpHeaderInfo.getContentType();

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

                return new Q2SyncAdapter(urlConnection);
            } catch (IOException e) {
                throw new BuildException("Can not build sync adapter.");
            }
        }
    }
}
