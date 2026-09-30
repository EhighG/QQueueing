package com.example.tes24.dto;

public record WaitingStatusResponse(Long myOrder, Integer totalQueueSize, String redirectUrl) {
    public boolean hasCurrentTurn() { return this.myOrder <= -1; }
}
