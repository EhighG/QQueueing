package com.qqueueing.main.waiting.service;

import com.qqueueing.main.registration.model.Registration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;

/**
 * 입장 처리. 활성 대기열마다 줄 앞에서 대기자를 꺼내 입장 기록에 넣는다.
 * 운영에서는 AdmissionScheduler가 1초마다 부르고, 테스트는 admitAll()을 직접 부른다.
 */
@Slf4j
@Service
public class AdmissionService {

    /** 대기열 하나가 1초에 입장시키는 인원. 지금은 입장 속도와 상관없이 고정이다. */
    static final long FIXED_ADMISSIONS_PER_SECOND = 100;

    private final QueueRegistry queueRegistry;
    private final QueueStore queueStore;
    private final Clock clock;

    public AdmissionService(QueueRegistry queueRegistry, QueueStore queueStore, Clock clock) {
        this.queueRegistry = queueRegistry;
        this.queueStore = queueStore;
        this.clock = clock;
    }

    /** 활성 대기열을 모두 돌며 이번 초의 입장을 처리한다. 한 대기열이 실패해도 나머지는 계속한다. */
    public void admitAll() {
        long epochSecond = clock.instant().getEpochSecond();
        for (Registration registration : queueRegistry.findActive()) {
            try {
                long count = admissionCount(registration, epochSecond);
                queueStore.admit(registration.getId(), count);
            } catch (RuntimeException e) {
                log.error("입장 처리 실패. queueId={}", registration.getId(), e);
            }
        }
    }

    /**
     * epochSecond 초에 이 대기열에서 입장시킬 인원.
     * 지금은 등록 정보의 입장 속도를 쓰지 않고 초당 100명으로 고정한다.
     */
    long admissionCount(Registration registration, long epochSecond) {
        return FIXED_ADMISSIONS_PER_SECOND;
    }
}
