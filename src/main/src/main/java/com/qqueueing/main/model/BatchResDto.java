package com.qqueueing.main.model;

import lombok.Getter;

import java.util.List;
import java.util.Set;

@Getter
public class BatchResDto {
    private List<String> curDoneSet;
    private Long batchLastIdx;
    private Long totalQueueSize;
}
