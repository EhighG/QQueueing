package com.example.tes24.qqueue_module.asyncadapter;

import com.example.tes24.qqueue_module.http.Q2HttpHeader;

public interface Q2AsyncAdapterFactory {
    Q2AsyncAdapter getInstance(Q2HttpHeader q2HttpHeader);
}
