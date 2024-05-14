package com.qqueueing.main.waiting.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Getter @Setter
public class WaitingStatusDto {
    private int partitionNo;
    private Set<String> doneSet = new HashSet<>();
    private List<Long> outList = new LinkedList<>();
    private int lastOffset;
    private int totalQueueSize;
    private String targetUrl;
    private AtomicInteger enterCnt;
    private int enterCntCapture;
//    private String cachedQueuePagePath;
//    private String cachedTargetPagePath;


    @Builder
    public WaitingStatusDto(int partitionNo, String targetUrl, int lastOffset, int totalQueueSize) {
        this.partitionNo = partitionNo;
        this.lastOffset = lastOffset;
        this.totalQueueSize = totalQueueSize;
        this.targetUrl = targetUrl;
        this.enterCnt = new AtomicInteger(0);
    }
}
