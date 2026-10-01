package com.qqueueing.main.waiting.model;

/**
 * 줄 서기 응답.
 *
 * @param partitionNo 대기열을 부르는 번호. 순번 조회와 이탈 요청에 그대로 보낸다.
 * @param waiterId    서버가 발급한 추측할 수 없는 대기자 ID
 * @param myOrder     줄을 선 직후의 순번(앞 인원 + 1)
 */
public record EnqueueResponse(Integer partitionNo, String waiterId, long myOrder) {
}
