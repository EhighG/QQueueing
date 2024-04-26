package com.example.tes24.qqueue_module.dto;

import java.io.Serializable;

public class Q2ServerResponse implements Serializable {
    private static final long serialVersionUID = 5619234618L;

    String clientId;
    String userId;
    Long userSequence;
    Long currentQueueSize;
    Long capacity;

    public boolean isFull() {
        return currentQueueSize == capacity;
    }
}
