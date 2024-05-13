package com.qqueueing.main.waiting.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetMyOrderResDto {
    private Long myOrder;
    private Long totalQueueSize;
    @Setter
    private String token;

    // on waiting
    public GetMyOrderResDto(Long myOrder, Long totalQueueSize) {
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
