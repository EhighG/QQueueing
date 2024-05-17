package com.qqueueing.main.waiting.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@ToString
public class BatchResDto {
    private List<String> curDoneList;
    private Integer lastOffset;
    private Integer totalQueueSize;
    private long currentOffset;
}
