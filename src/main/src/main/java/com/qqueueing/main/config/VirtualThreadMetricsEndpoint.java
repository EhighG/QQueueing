package com.qqueueing.main.config;

import com.qqueueing.main.waiting.model.VirtualThreadMetrics;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint;
import org.springframework.stereotype.Component;

import java.util.concurrent.ForkJoinPool;

@Component
@RequiredArgsConstructor
@WebEndpoint(id = "virtual-thread-metrics")
public class VirtualThreadMetricsEndpoint {

    private final ForkJoinPool forkJoinPool;

    @ReadOperation
    public VirtualThreadMetrics getVirtualThreadMetrics() {
        VirtualThreadMetrics metrics = new VirtualThreadMetrics();
        metrics.setActiveThreadCount(forkJoinPool.getActiveThreadCount());
        metrics.setQueuedTaskCount(forkJoinPool.getQueuedTaskCount());
        metrics.setParallelism(forkJoinPool.getParallelism());
        metrics.setPoolSize(forkJoinPool.getPoolSize());
        metrics.setRunningThreadCount(forkJoinPool.getRunningThreadCount());
        metrics.setStealCount(forkJoinPool.getStealCount());
        metrics.setQueuedSubmissionCount(forkJoinPool.getQueuedSubmissionCount());
        metrics.setAsyncMode(forkJoinPool.getAsyncMode());
        // 기타 원하는 가상 스레드 메트릭 설정

        return metrics;
    }
}