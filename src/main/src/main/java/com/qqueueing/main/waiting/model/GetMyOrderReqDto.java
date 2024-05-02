package com.qqueueing.main.waiting.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GetMyOrderReqDto {
    private Long waitingIdx;
    private String idVal;
    private String topicName;
}
