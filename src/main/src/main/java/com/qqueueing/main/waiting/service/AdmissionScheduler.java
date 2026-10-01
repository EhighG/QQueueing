package com.qqueueing.main.waiting.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 매초 정각에 입장 처리를 부른다. 테스트는 admission.scheduler.enabled=false로 끄고 AdmissionService를 직접 부른다.
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "admission.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class AdmissionScheduler {

    private final AdmissionService admissionService;
    // 가상 스레드 스케줄러는 cron 작업을 회차마다 새 스레드에서 돌리므로, 앞 회차가 끝나지 않았으면 이번 회차를 건너뛴다.
    private final ReentrantLock running = new ReentrantLock();

    public AdmissionScheduler(AdmissionService admissionService) {
        this.admissionService = admissionService;
    }

    @Scheduled(cron = "* * * * * *")
    public void admitEverySecond() {
        if (!running.tryLock()) {
            return;
        }
        try {
            admissionService.admitAll();
        } catch (RuntimeException e) {
            log.error("입장 처리 실패", e);
        } finally {
            running.unlock();
        }
    }
}
