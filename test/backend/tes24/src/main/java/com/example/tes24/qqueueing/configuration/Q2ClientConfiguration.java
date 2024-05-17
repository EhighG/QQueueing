package com.example.tes24.qqueueing.configuration;

import com.example.tes24.qqueueing.Q2Client;
import com.example.tes24.qqueueing.annotation.Q2AutoConfiguration;
import com.example.tes24.qqueueing.annotation.Q2Bean;
import com.example.tes24.qqueueing.adapter.async.Q2AsyncAdapter;
import com.example.tes24.qqueueing.adapter.sync.Q2SyncAdapter;

@Q2AutoConfiguration
public class Q2ClientConfiguration {
    @Q2Bean
    public Q2Client q2Client(Q2SyncAdapter q2SyncAdapter, Q2AsyncAdapter q2AsyncAdapter) {
        return new Q2Client(q2SyncAdapter, q2AsyncAdapter);
    }
}
