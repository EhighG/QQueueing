package com.example.tes24.qqueueing.adapter.async;

import com.example.tes24.qqueueing.channel.Q2HttpHeader;

public interface Q2AsyncAdapterFactory {
    Q2AsyncAdapter getInstance(Q2HttpHeader q2HttpHeader);
}
