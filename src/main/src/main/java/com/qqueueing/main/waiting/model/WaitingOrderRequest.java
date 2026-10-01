package com.qqueueing.main.waiting.model;

/**
 * 순번 조회 요청.
 */
public record WaitingOrderRequest(Integer partitionNo, String waiterId) {
}
