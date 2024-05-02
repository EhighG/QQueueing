package com.qqueueing.main.waiting.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
public class BatchResDto {
    private List<String> curDoneList;
    private Integer lastOffset;
    private Integer totalQueueSize;
}
