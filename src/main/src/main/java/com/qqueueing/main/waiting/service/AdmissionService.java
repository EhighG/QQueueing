package com.qqueueing.main.waiting.service;

import com.qqueueing.main.registration.model.Registration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;

/**
 * 입장 처리. 활성 대기열마다 그 초의 몫만큼 줄 앞에서 대기자를 꺼내 입장 기록에 넣는다.
 * 운영에서는 AdmissionScheduler가 1초마다 부르고, 테스트는 admitAll()을 직접 부른다.
 */
@Slf4j
@Service
public class AdmissionService {

    private static final long SECONDS_PER_MINUTE = 60;

    private final QueueRegistry queueRegistry;
    private final QueueStore queueStore;
    private final Clock clock;

    public AdmissionService(QueueRegistry queueRegistry, QueueStore queueStore, Clock clock) {
        this.queueRegistry = queueRegistry;
        this.queueStore = queueStore;
        this.clock = clock;
    }

    /**
     * 활성 대기열을 모두 돌며 이번 초의 입장을 처리한다. 한 대기열이 실패해도 나머지는 계속한다.
     * 입장 속도는 매번 캐시(QueueRegistry)의 등록 정보에서 읽으므로, 고친 입장 속도는 다음 호출부터 쓰인다.
     */
    public void admitAll() {
        long epochSecond = clock.instant().getEpochSecond();
        for (Registration registration : queueRegistry.findActive()) {
            try {
                long count = admissionCount(registration.admissionRatePerMinute(), epochSecond);
                queueStore.admit(registration.getId(), count);
            } catch (RuntimeException e) {
                log.error("입장 처리 실패. queueId={}", registration.getId(), e);
            }
        }
    }

    /**
     * 입장 속도가 분당 ratePerMinute명일 때 epochSecond(T)초에 입장시킬 인원.
     * floor(r × T / 60) − floor(r × (T − 1) / 60)이다. 그래서 어느 60초 구간에서든 정확히 r명이 입장하고,
     * 소수점 아래 몫만 다음 초로 넘어간다. 그 초의 몫만 계산하므로 대기자가 적어 남은 몫이나 건너뛴 초의 몫은 넘어가지 않는다.
     * T = 60q + s(0 ≤ s < 60)로 나누면 floor(r × T / 60) = r × q + floor(r × s / 60)이고, r × q는 두 항에서 지워진다.
     * 그래서 r × T를 곱하지 않고 s만으로 계산한다. r × s는 int 최댓값 × 59보다 작아 long을 넘지 않는다.
     */
    static long admissionCount(int ratePerMinute, long epochSecond) {
        long s = Math.floorMod(epochSecond, SECONDS_PER_MINUTE);
        return Math.floorDiv(ratePerMinute * s, SECONDS_PER_MINUTE)
                - Math.floorDiv(ratePerMinute * (s - 1), SECONDS_PER_MINUTE);
    }
}
