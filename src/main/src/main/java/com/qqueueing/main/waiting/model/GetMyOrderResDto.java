package com.qqueueing.main.waiting.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetMyOrderResDto {
    private Long myOrder;
    private Integer totalQueueSize;

    // on waiting
    public GetMyOrderResDto(Long myOrder, Integer totalQueueSize) {
        this.myOrder = myOrder;
        this.totalQueueSize = totalQueueSize;
    }
}
