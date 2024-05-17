package com.qqueueing.main.registration.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GetWaitingInfoResDto {
    private long enterCnt;
    private long totalQueueSize;

    public GetWaitingInfoResDto(long enterCnt, long totalQueueSize) {
        this.enterCnt = enterCnt;
        this.totalQueueSize = totalQueueSize;
    }
}
