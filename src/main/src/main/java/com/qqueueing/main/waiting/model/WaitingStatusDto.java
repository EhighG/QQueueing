package com.qqueueing.main.waiting.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Getter @Setter
public class WaitingStatusDto {
    private int partitionNo;
    private Set<String> doneSet = new HashSet<>();
    private List<Long> outList = new LinkedList<>();
    private int lastOffset;
    private int totalQueueSize;
    private String targetUrl;

    @Builder
    public WaitingStatusDto(int partitionNo, String targetUrl, int lastOffset, int totalQueueSize) {
        this.partitionNo = partitionNo;
        this.lastOffset = lastOffset;
        this.totalQueueSize = totalQueueSize;
    }
}
