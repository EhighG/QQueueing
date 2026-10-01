package com.qqueueing.main.waiting.model;

/**
 * 순번 조회 응답.
 *
 * @param status         대기자 상태
 * @param myOrder        순번(앞 인원 + 1). WAITING일 때만 1 이상이고, 그 밖에는 0이다.
 * @param totalQueueSize 대기 인원(지금 줄에 있는 대기자 수)
 * @param token          통과 토큰. ENTERED일 때만 있고, 그 밖에는 null이다.
 */
public record WaitingOrderResponse(WaitingStatus status, long myOrder, long totalQueueSize, String token) {

    public static WaitingOrderResponse notFound(long totalQueueSize) {
        return new WaitingOrderResponse(WaitingStatus.NOT_FOUND, 0, totalQueueSize, null);
    }
}
