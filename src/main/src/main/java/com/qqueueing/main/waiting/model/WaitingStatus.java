package com.qqueueing.main.waiting.model;

/**
 * 순번 조회 결과로 본 대기자의 상태.
 */
public enum WaitingStatus {
    /** 줄에 있다. 순번과 대기 인원을 함께 준다. */
    WAITING,
    /** 입장했다. 이번 응답에 통과 토큰이 한 번만 담긴다. */
    ENTERED,
    /** 대기자 없음. 줄에도 입장 기록에도 없다(모르는 대기자 ID, 이탈, 이미 통과 토큰을 받음, 대기열이 비워짐). */
    NOT_FOUND
}
