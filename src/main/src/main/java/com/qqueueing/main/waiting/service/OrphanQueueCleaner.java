package com.qqueueing.main.waiting.service;

import com.qqueueing.main.registration.model.Registration;
import com.qqueueing.main.registration.repository.RegistrationRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * 기동 정리. main이 기동할 때 MongoDB 등록 정보에 없는 대기열 id의 Redis 상태를 지운다.
 * 삭제 도중 Redis 정리가 실패했거나, docker compose down으로 MongoDB만 비워진 경우에 남는 상태를 치운다.
 * 등록 정보가 있는 대기열의 상태는 건드리지 않는다. 그래서 main을 재시작해도 줄을 그대로 이어 쓴다.
 */
@Slf4j
@Component
public class OrphanQueueCleaner {

    private final RegistrationRepository registrationRepository;
    private final QueueStore queueStore;

    public OrphanQueueCleaner(RegistrationRepository registrationRepository, QueueStore queueStore) {
        this.registrationRepository = registrationRepository;
        this.queueStore = queueStore;
    }

    /**
     * 빈 초기화 단계에서 한 번 실행한다. 이때는 아직 웹 요청을 받지 않고 1초마다 도는 입장 처리도 시작하지 않았으므로,
     * 정리하는 동안 새로 등록된 대기열의 상태를 지우는 일이 없다.
     * 정리에 실패해도 기동은 계속한다. 남은 상태는 다음 기동 때 다시 정리한다.
     */
    @PostConstruct
    void cleanUpOnStartup() {
        try {
            cleanUp();
        } catch (RuntimeException e) {
            log.warn("기동 정리 실패. 등록 정보가 없는 대기열의 Redis 상태는 다음 기동 때 다시 정리한다.", e);
        }
    }

    /** 등록 정보가 없는 대기열의 Redis 상태를 지운다. 테스트는 이 메서드를 직접 부른다. */
    public void cleanUp() {
        Set<String> registeredQueueIds = registrationRepository.findAll().stream()
                .map(Registration::getId)
                .collect(Collectors.toSet());
        Set<String> removed = queueStore.deleteAllExcept(registeredQueueIds);
        log.info("기동 정리: 등록 정보가 없는 대기열 {}개의 Redis 상태를 지웠다. queueIds={}", removed.size(), removed);
    }
}
