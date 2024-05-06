package com.qqueueing.main.waiting.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetMyOrderResDto {
    private Long myOrder;
    private Integer totalQueueSize;
    @Setter
    private String tempToken;

    // on waiting
    public GetMyOrderResDto(Long myOrder, Integer totalQueueSize) {
        this.myOrder = myOrder;
        this.totalQueueSize = totalQueueSize;
    }
//
//    public GetMyOrderResDto(Long myOrder, Integer totalQueueSize, String tempToken) {
//        this.myOrder = myOrder;
//        this.totalQueueSize = totalQueueSize;
//        this.tempToken = tempToken;
//    }
}
