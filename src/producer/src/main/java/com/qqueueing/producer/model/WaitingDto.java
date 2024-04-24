package com.qqueueing.producer.model;


import lombok.Getter;

@Getter
public class WaitingDto {

    private Long waitingIdx;
    private String ip;
    public WaitingDto(Long waitingIdx, String ip) {
        this.waitingIdx = waitingIdx;
        this.ip = ip;
    }
}
