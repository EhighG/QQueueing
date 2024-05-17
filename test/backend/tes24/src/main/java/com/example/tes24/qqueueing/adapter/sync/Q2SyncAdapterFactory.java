package com.example.tes24.qqueueing.adapter.sync;

import com.example.tes24.qqueueing.channel.Q2HttpHeader;

public interface Q2SyncAdapterFactory {
    Q2SyncAdapter getInstance(Q2HttpHeader q2HttpHeader);
}
