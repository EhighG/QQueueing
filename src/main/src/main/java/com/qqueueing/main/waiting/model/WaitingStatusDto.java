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
    private String topicName;
    private Set<String> doneSet = new HashSet<>();
    private List<Long> outList = new LinkedList<>();
    private int lastOffset;
    private int totalQueueSize;

    @Builder
    public WaitingStatusDto(String topicName, int lastOffset, int totalQueueSize) {
        this.topicName = topicName;
        this.lastOffset = lastOffset;
        this.totalQueueSize = totalQueueSize;
    }
}
