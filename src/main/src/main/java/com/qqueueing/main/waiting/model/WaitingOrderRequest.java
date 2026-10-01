package com.qqueueing.main.waiting.model;

/**
 * 순번 조회 요청.
 *
 * @param queueId  대기열 id(등록 정보 id). 줄 서기 응답의 queueId를 그대로 보낸다.
 * @param waiterId 줄 서기 응답의 대기자 ID
 */
public record WaitingOrderRequest(String queueId, String waiterId) {
}
