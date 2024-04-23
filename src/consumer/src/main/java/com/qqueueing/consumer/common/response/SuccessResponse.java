package com.qqueueing.consumer.common.response;

import lombok.Getter;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@AllArgsConstructor
@Builder
public class SuccessResponse {

    int status;
    String message;
    Object result;

    public SuccessResponse(int status, String message){
        this.status = status;
        this.message = message;
    }
}
