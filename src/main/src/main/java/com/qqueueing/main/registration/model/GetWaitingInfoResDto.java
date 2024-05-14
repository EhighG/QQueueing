package com.qqueueing.main.registration.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GetWaitingInfoResDto {
    private int enterCnt;
    private int totalQueueSize;

    public GetWaitingInfoResDto(int enterCnt, int totalQueueSize) {
        this.enterCnt = enterCnt;
        this.totalQueueSize = totalQueueSize;
    }
}
