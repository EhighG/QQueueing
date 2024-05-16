package com.qqueueing.main.registration.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GetWaitingInfoResDto {
    private long enterCnt;
    private int totalQueueSize;

    public GetWaitingInfoResDto(long enterCnt, int totalQueueSize) {
        this.enterCnt = enterCnt;
        this.totalQueueSize = totalQueueSize;
    }
}
