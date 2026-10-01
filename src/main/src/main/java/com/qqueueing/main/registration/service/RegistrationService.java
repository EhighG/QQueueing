package com.qqueueing.main.registration.service;

import com.qqueueing.main.registration.model.Registration;
import com.qqueueing.main.registration.model.RegistrationUpdateRequest;
import com.qqueueing.main.registration.model.GetWaitingInfoResDto;
import com.qqueueing.main.registration.repository.RegistrationRepository;
import com.qqueueing.main.waiting.service.WaitingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RegistrationService {

//    @Autowired
    private RegistrationRepository registrationRepository;
    private final WaitingService waitingService;
    private final ScriptExecService scriptExecService;
    private final int MAX_PARTITION_INDEX;
    public RegistrationService(RegistrationRepository registrationRepository, ScriptExecService scriptExecService, WaitingService waitingService,
                               @Value("${kafka.partition.max-index}") int maxPartitionIndex) {
        this.registrationRepository = registrationRepository;
        this.scriptExecService = scriptExecService;
        this.waitingService = waitingService;
        this.MAX_PARTITION_INDEX = maxPartitionIndex;
    }

    public Registration createRegistration(Registration registration) {
        // 카프카에 저장할 빈 공간(=파티션) 키를 찾는다.
        registration.setPartitionNo(findEmptyPartitionNo());
        // 등록하면 대기열이 바로 활성 상태가 된다
        registration.setIsActive(true);
        // DB 저장
        Registration savedRegistration = registrationRepository.save(registration);
        // 등록 스크립트 실행
        scriptExecService.execShell(savedRegistration.getTargetUrl(), "register");
        waitingService.onRegistrationSaved(savedRegistration);
        return savedRegistration;
    }

    public List<Registration> getAllRegistrations() {
        return registrationRepository.findAll();
    }

    public Registration getRegistrationById(String id) throws ChangeSetPersister.NotFoundException {
        return registrationRepository.findById(id)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());
    }

    public Registration updateRegistrationById(String id, RegistrationUpdateRequest request) throws ChangeSetPersister.NotFoundException {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());
        registration.update(request.getTargetUrl(), request.getMaxCapacity(), request.getProcessingPerMinute(), request.getServiceName(), request.getQueueImageUrl());
        Registration savedRegistration = registrationRepository.save(registration);
        waitingService.onRegistrationSaved(savedRegistration);
        return savedRegistration;
    }

    public void deleteRegistrationById(String id) throws ChangeSetPersister.NotFoundException {
        if (!registrationRepository.existsById(id)) {
            throw new ChangeSetPersister.NotFoundException();
        }
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());
        scriptExecService.execShell(registration.getTargetUrl(), "delete");
        registrationRepository.deleteById(id);
        // 등록 정보를 지운 뒤 그 대기열의 Redis 상태를 지운다
        waitingService.onRegistrationDeleted(id);
    }

    public GetWaitingInfoResDto getWaitingInfo(String id) throws ChangeSetPersister.NotFoundException {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);
        return waitingService.getWaitingInfo(registration.getId());
    }

    public String getImageById(String id) throws ChangeSetPersister.NotFoundException {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());
        return registration.getQueueImageUrl();
    }

    public String getImageByTargetUrl(String targetUrl) {
        Registration registration = registrationRepository.findByTargetUrl(targetUrl);
        if (registration == null || registration.getQueueImageUrl() == null) {
            return null;
        }
        return registration.getQueueImageUrl();
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
