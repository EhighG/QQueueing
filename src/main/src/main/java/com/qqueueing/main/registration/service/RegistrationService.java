package com.qqueueing.main.registration.service;

import com.qqueueing.main.registration.model.Registration;
import com.qqueueing.main.registration.repository.RegistrationRepository;
import com.qqueueing.main.waiting.service.WaitingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RegistrationService {

//    @Autowired
    private RegistrationRepository registrationRepository;
    private final WaitingService waitingService;
    private final int MAX_PARTITION_INDEX;
    public RegistrationService(RegistrationRepository registrationRepository, WaitingService waitingService,
                               @Value("${kafka.partition.max-index}") int maxPartitionIndex) {
        this.registrationRepository = registrationRepository;
        this.waitingService = waitingService;
        this.MAX_PARTITION_INDEX = maxPartitionIndex;
    }

    public Registration createRegistration(Registration registration) {
        // 카프카에 저장할 빈 공간(=파티션) 키를 찾는다.
        registration.setPartitionNo(findEmptyPartitionNo());
        Registration savedRegistration = registrationRepository.save(registration);
        // 대기열 기능에서 쓰이는 url-파티션 키 매핑에 추가한다.
        waitingService.addUrlPartitionMapping(registration.getTargetUrl());
        return savedRegistration;
    }

    public List<Registration> getAllRegistrations() {
        return registrationRepository.findAll();
    }

    private int findEmptyPartitionNo() {
        Set<Integer> assigned = registrationRepository.findAll().stream()
                .map(Registration::getPartitionNo)
                .collect(Collectors.toSet());
        for (int i = 0; i < MAX_PARTITION_INDEX; i++) {
            if (!assigned.contains(i)) {
                return i;
            }
        }
        // 모든 파티션이 사용중일 때
        throw new RuntimeException("url을 더 이상 추가할 수 없습니다.");
    }
}
