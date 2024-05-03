package com.example.tes24.qqueue_module;

import com.example.tes24.qqueue_module.http.ContentType;
import com.example.tes24.qqueue_module.syncadapter.*;
import com.example.tes24.qqueue_module.dto.Q2ClientRequest;
import com.example.tes24.qqueue_module.http.Q2HttpHeader;
import com.example.tes24.qqueue_module.dto.Q2ServerResponse;
import com.example.tes24.qqueue_module.http.urlconnection.HttpURLConnectionFactory;
import com.example.tes24.qqueue_module.http.urlconnection.HttpURLConnectionFactoryImpl;

import java.net.HttpURLConnection;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Client to perform HTTP request based on {@link HttpURLConnection}.
 * Conduct requests through its own {@link Q2AdapterDelegator}
 * which has factories for construct adapter instances.
 *
 * <p>{@link Q2AdapterDelegator} will delegate requests to suitable factory
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
        Q2Monitoring.initiateTimer();
    }

    private static final Q2Client instance;
    private static final ReentrantLock reentrantLock;
    private final Q2AdapterDelegator delegator;

    private Q2Client() {
        delegator = new Q2AdapterDelegator(Q2Context.getQ2SyncAdapterFactories());
    }

    public static Q2Client getQ2Client() {
         assert instance != null;

        return instance;
    }

    public Q2ServerResponse request(Q2HttpHeader httpHeader, Q2ClientRequest request) {
        assert httpHeader != null;

        reentrantLock.lock();
        try {
            return delegator.delegate(httpHeader, request);
        } finally {
            reentrantLock.unlock();
        }
    }

    static class Q2Monitoring {
        private static final Timer timer = new Timer();

        private static final Q2HttpHeader timetQ2HttpHeader = Q2HttpHeader.fromProperties(Q2Context.getMonitorHeaderProperties());

        private static final Q2ClientRequest timerQ2ClientRequest = new Q2ClientRequest();

        private static final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Q2Client.getQ2Client().request(timetQ2HttpHeader, timerQ2ClientRequest);
            }
        };

        private static void initiateTimer() {
            timer.schedule(timerTask, new Date(), 5000);
        }

        private void terminateTimer() {
            timer.cancel();
        }
    }
}
