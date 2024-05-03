package com.example.tes24.qqueue_module.adapter;

import com.example.tes24.qqueue_module.adapter.Q2SyncAdapter;
import com.example.tes24.qqueue_module.dto.Q2HttpHeader;

public interface Q2SyncAdapterFactory {
    Q2SyncAdapter getInstance(Q2HttpHeader q2HttpHeader);
}
