package com.example.tes24.dto;

public record WaitingStatusResponseRecord(Long myOrder, Integer totalQueueSize, String redirectUrl) {
}
