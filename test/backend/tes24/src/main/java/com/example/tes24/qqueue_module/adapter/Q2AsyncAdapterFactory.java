package com.example.tes24.qqueue_module.adapter;

import com.example.tes24.qqueue_module.adapter.Q2AsyncAdapter;
import com.example.tes24.qqueue_module.dto.Q2HttpHeader;

public interface Q2AsyncAdapterFactory {
    Q2AsyncAdapter getInstance(Q2HttpHeader q2HttpHeader);
}
