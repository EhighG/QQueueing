package com.qqueueing.main.waiting.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GetMyOrderReqDto {
    private Long order;
    private String idVal;
    private Integer partitionNo;
}
