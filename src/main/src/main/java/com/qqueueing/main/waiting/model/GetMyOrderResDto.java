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


    // on waiting
    public GetMyOrderResDto(Long myOrder, Integer totalQueueSize, int enterCnt) {
        this.myOrder = myOrder;
        this.totalQueueSize = totalQueueSize;
        this.enterCnt = enterCnt;
    }


    //
//    public GetMyOrderResDto(Long myOrder, Integer totalQueueSize, String tempToken) {
//        this.myOrder = myOrder;
//        this.totalQueueSize = totalQueueSize;
//        this.tempToken = tempToken;
//    }
}
