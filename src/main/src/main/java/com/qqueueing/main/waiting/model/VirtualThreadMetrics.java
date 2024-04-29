package com.qqueueing.main.waiting.model;

import lombok.Data;

@Data
public class VirtualThreadMetrics {

    private int activeThreadCount;
    private long queuedTaskCount;
    private int parallelism;
    private int poolSize;
    private int runningThreadCount;
    private long stealCount;
    private int queuedSubmissionCount;
    private boolean asyncMode;

}