package com.qqueueing.main.waiting.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetMyOrderResDto {
    private Long myOrder;
    private Integer totalQueueSize;
    private String redirectUrl;

    // on waiting
    public GetMyOrderResDto(Long myOrder, Integer totalQueueSize) {
        this.myOrder = myOrder;
        this.totalQueueSize = totalQueueSize;
    }

    // on waiting done

    public GetMyOrderResDto(Long myOrder, Integer totalQueueSize, String redirectUrl) {
        this.myOrder = myOrder;
        this.totalQueueSize = totalQueueSize;
        this.redirectUrl = redirectUrl;
    }
}
