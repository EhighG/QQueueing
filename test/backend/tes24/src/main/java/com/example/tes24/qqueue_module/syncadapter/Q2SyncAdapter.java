package com.example.tes24.qqueue_module.syncadapter;

import com.example.tes24.qqueue_module.dto.Q2ClientRequest;
import com.example.tes24.qqueue_module.dto.Q2ServerResponse;

/**
 * Adapter for communicate with The QQueueing api server.
 *
 * <p>Require {@link Q2ClientRequest} for requests.
 * If request success, {@link Q2ServerResponse} will be return.
 *
 * @since 1.0
 * @author Moon-Young Shin
 */
public interface Q2SyncAdapter {
    Q2ServerResponse request(Q2ClientRequest q2ClientRequest);
}
