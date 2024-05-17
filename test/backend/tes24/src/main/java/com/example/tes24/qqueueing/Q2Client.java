package com.example.tes24.qqueueing;

import com.example.tes24.qqueueing.adapter.async.Q2AsyncAdapter;
import com.example.tes24.qqueueing.adapter.sync.Q2SyncAdapter;
import com.example.tes24.qqueueing.dto.Q2ClientRequest;
import com.example.tes24.qqueueing.dto.Q2ServerResponse;

import java.util.concurrent.CompletableFuture;

/**
 * The HTTP client to perform HTTP request.
 *
 * <p>It has two kinds of adapter, {@link Q2SyncAdapter} and {@link Q2SyncAdapter}.
 * A client will delegate request to its adapter.
 *
 * @since 1.1
 * @author Moon-Young Shin
 */
public class Q2Client {
    public Q2Client(Q2SyncAdapter q2SyncAdapter, Q2AsyncAdapter q2AsyncAdapter) {
        this.q2SyncAdapter = q2SyncAdapter;
        this.q2AsyncAdapter = q2AsyncAdapter;
    }

    private final Q2SyncAdapter q2SyncAdapter;
    private final Q2AsyncAdapter q2AsyncAdapter;

    public Q2ServerResponse request(Q2ClientRequest request) {
        return q2SyncAdapter.request(request);
    }

    public CompletableFuture<Q2ServerResponse> requestAsync(Q2ClientRequest request) {
        return q2AsyncAdapter.requestAsync(request);
    }
}
