package com.example.tes24.qqueue_module.adapter;

abstract class AbstractQ2AdapterFactory implements Q2AdapterFactory {
    public Q2Adapter getInstance(HttpHeaderInfo httpHeaderInfo) {
        return createQ2Adapter(httpHeaderInfo);
    }

    abstract protected Q2Adapter createQ2Adapter(HttpHeaderInfo httpHeaderInfo);

    protected abstract static class AbstractQ2AdapterBuilder {
        HttpHeaderInfo httpHeaderInfo;


        abstract AbstractQ2AdapterBuilder url(String url);

        abstract AbstractQ2AdapterBuilder timeout(int timeout);

        abstract AbstractQ2AdapterBuilder method(String method);

        abstract AbstractQ2AdapterBuilder contentType(ContentType contentType);

        abstract Q2Adapter build();
    }
}
