package com.qqueueing.main.waiting.service;

import com.qqueueing.main.registration.model.Registration;
import com.qqueueing.main.registration.repository.RegistrationRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MongoDB 등록 정보의 메모리 캐시. 대기열 id(등록 정보 id)나 대상 URL로 대기열을 찾는 데 쓴다.
 * MongoDB에서 다시 읽어 올 수 있는 값만 둔다. 대기열 상태는 QueueStore(Redis)에 있다.
 * 등록 정보를 바꾸는 쪽은 저장한 뒤 put/remove로 이 캐시를 맞춘다.
 */
@Component
public class QueueRegistry {

    private final RegistrationRepository registrationRepository;
    private final Map<String, Registration> registrations = new ConcurrentHashMap<>();

    public QueueRegistry(RegistrationRepository registrationRepository) {
        this.registrationRepository = registrationRepository;
    }

    /** 기동할 때 등록 정보를 읽어 온다. Redis의 대기열 상태는 건드리지 않는다. */
    @PostConstruct
    public void load() {
        registrationRepository.findAll().forEach(this::put);
    }

    public void put(Registration registration) {
        registrations.put(registration.getId(), registration);
    }

    public void remove(String queueId) {
        registrations.remove(queueId);
    }

    /** 대기열 id로 찾는다. id가 null이면 빈 값을 돌려준다(ConcurrentHashMap은 null 키를 받지 않는다). */
    public Optional<Registration> findById(String queueId) {
        if (queueId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(registrations.get(queueId));
    }

    public Optional<Registration> findByTargetUrl(String targetUrl) {
        return registrations.values().stream()
                .filter(r -> Objects.equals(r.getTargetUrl(), targetUrl))
                .findFirst();
    }

    public List<Registration> findActive() {
        return registrations.values().stream()
                .filter(r -> Boolean.TRUE.equals(r.getIsActive()))
                .toList();
    }
}
