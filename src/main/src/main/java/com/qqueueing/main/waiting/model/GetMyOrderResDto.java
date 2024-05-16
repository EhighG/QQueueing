package com.qqueueing.main.waiting.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetMyOrderResDto {
    private Long myOrder;
    private Integer totalQueueSize;
    private int enterCnt;
    @Setter
    private String token;

    // test
    private Long oldOrder;
    private int outCntInFront;
    private int lastOffset;


    // on waiting
    public GetMyOrderResDto(Long myOrder, Integer totalQueueSize, int enterCnt) {
        this.myOrder = myOrder;
        this.totalQueueSize = totalQueueSize;
        this.enterCnt = enterCnt;
    }

    public void update(Long oldOrder, int outCntInFront, int lastOffset) {
        this.oldOrder = oldOrder;
        this.outCntInFront = outCntInFront;
        this.lastOffset = lastOffset;
    }

    //
//    public GetMyOrderResDto(Long myOrder, Integer totalQueueSize, String tempToken) {
//        this.myOrder = myOrder;
//        this.totalQueueSize = totalQueueSize;
//        this.tempToken = tempToken;
//    }
}
