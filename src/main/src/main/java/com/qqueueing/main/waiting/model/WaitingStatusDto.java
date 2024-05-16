package com.qqueueing.main.waiting.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

@Getter @Setter
public class WaitingStatusDto {
    private int partitionNo;
    private Set<String> doneSet = new HashSet<>();
    private List<Long> outList = new LinkedList<>();
    private long currentOffset;
    private int totalQueueSize;
    private String targetUrl;
    private AtomicLong enterCnt;
    private long enterCntCapture;
    private int enterCntOfLastTime;
//    private String cachedQueuePagePath;
//    private String cachedTargetPagePath;


    @Builder
    public WaitingStatusDto(int partitionNo, String targetUrl, int currentOffset, int totalQueueSize) {
        this.partitionNo = partitionNo;
        this.currentOffset = currentOffset;
        this.totalQueueSize = totalQueueSize;
        this.targetUrl = targetUrl;
        this.enterCnt = new AtomicLong(0);
    }
}
