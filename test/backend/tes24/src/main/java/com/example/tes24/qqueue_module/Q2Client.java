package com.example.tes24.qqueue_module;

import com.example.tes24.qqueue_module.syncadapter.*;
import com.example.tes24.qqueue_module.dto.Q2ClientRequest;
import com.example.tes24.qqueue_module.http.Q2HttpHeader;
import com.example.tes24.qqueue_module.dto.Q2ServerResponse;
import com.example.tes24.qqueue_module.http.urlconnection.HttpURLConnectionFactory;
import com.example.tes24.qqueue_module.http.urlconnection.HttpURLConnectionFactoryImpl;

import java.net.HttpURLConnection;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Client to perform HTTP request based on {@link HttpURLConnection}.
 * Conduct requests through its own {@link Q2SyncAdapterDelegator}
 * which has factories for construct adapter instances.
 *
 * <p>{@link Q2SyncAdapterDelegator} will delegate requests to suitable factory
 * and then factory construct {@link Q2SyncAdapter} instance using {@link HttpURLConnection} which made by {@link HttpURLConnectionFactory}.
 *
 * <p>Use static factory method {@link #getQ2Client()} to get default {@link Q2Client} instance.
 * The instance has {@link Q2SyncAdapterFactoryImpl} factory which has {@link HttpURLConnectionFactoryImpl} for create {@link Q2SyncAdapterImpl}.
 *
 * @since 1.0
 * @author Moon-Young Shin
 */
public class Q2Client {
    static {
        Q2ConfigurationLoader.load();
        instance = new Q2Client();
        reentrantLock = new ReentrantLock();
    }

    private static final Q2Client instance;
    private static final ReentrantLock reentrantLock;
    private final Q2SyncAdapterDelegator q2SyncAdapterDelegator;
    private final Q2AsyncAdapterDelegator q2AsyncAdapterDelegator;

    private Q2Client() {
        q2SyncAdapterDelegator = new Q2SyncAdapterDelegator(Q2Context.getQ2SyncAdapterFactories());
        q2AsyncAdapterDelegator = new Q2AsyncAdapterDelegator(Q2Context.getQ2AsyncAdapterFactories());
    }

    public static Q2Client getQ2Client() {
        return instance;
    }

    public Q2ServerResponse request(Q2HttpHeader httpHeader, Q2ClientRequest request) {
        reentrantLock.lock();
        try {
            return q2SyncAdapterDelegator.delegate(httpHeader, request);
        } finally {
            reentrantLock.unlock();
        }
    }

    public Future<Q2ServerResponse> requestAsync(Q2HttpHeader httpHeader, Q2ClientRequest request) {
        reentrantLock.lock();
        try {
            return q2AsyncAdapterDelegator.delegate(httpHeader, request);
        } finally {
            reentrantLock.unlock();
        }
    }
}
