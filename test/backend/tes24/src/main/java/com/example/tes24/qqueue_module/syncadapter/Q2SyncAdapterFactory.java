package com.example.tes24.qqueue_module.syncadapter;

import com.example.tes24.qqueue_module.http.Q2HttpHeader;

public interface Q2SyncAdapterFactory {
    Q2SyncAdapter getInstance(Q2HttpHeader q2HttpHeader);
}
