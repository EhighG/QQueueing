package com.qqueueing.main.sse;

import lombok.*;

@Getter
public class WaitingInfo {
    private Integer totalNum;
    private Long myOrder;

    @Builder
    public WaitingInfo(Integer totalNum, Long myOrder) {
        this.totalNum = totalNum;
        this.myOrder = myOrder;
    }
}
