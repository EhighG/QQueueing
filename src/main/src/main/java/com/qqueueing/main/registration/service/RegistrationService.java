package com.qqueueing.main.registration.service;

import com.qqueueing.main.registration.model.Registration;
import com.qqueueing.main.registration.model.RegistrationUpdateRequest;
import com.qqueueing.main.registration.model.GetWaitingInfoResDto;
import com.qqueueing.main.registration.repository.RegistrationRepository;
import com.qqueueing.main.waiting.service.WaitingService;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegistrationService {

//    @Autowired
    private RegistrationRepository registrationRepository;
    private final WaitingService waitingService;
    private final ScriptExecService scriptExecService;
    public RegistrationService(RegistrationRepository registrationRepository, ScriptExecService scriptExecService, WaitingService waitingService) {
        this.registrationRepository = registrationRepository;
        this.scriptExecService = scriptExecService;
        this.waitingService = waitingService;
    }

    public Registration createRegistration(Registration registration) {
        // 입장 속도는 저장하거나 에이전트에 알리기 전에 검사한다. 입력하지 않았으면 기본값(분당 6000명)을 넣는다.
        validateProcessingPerMinute(registration.getProcessingPerMinute());
        registration.fillDefaultProcessingPerMinute();
        // 등록하면 대기열이 바로 활성 상태가 된다. 대기열은 저장할 때 MongoDB가 만든 id로 부른다.
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
        // 입장 속도를 보냈으면 1 이상이어야 한다. 보내지 않으면 지금 값을 그대로 둔다.
        validateProcessingPerMinute(request.getProcessingPerMinute());
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());
        registration.update(request.getTargetUrl(), request.getMaxCapacity(), request.getProcessingPerMinute(), request.getServiceName(), request.getQueueImageUrl());
        Registration savedRegistration = registrationRepository.save(registration);
        // 캐시의 등록 정보를 바꾸므로 고친 입장 속도는 다음 입장 처리부터 쓰인다.
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

    /** 입장 속도(분당 입장 인원)는 1 이상이어야 한다. null(입력하지 않음)은 통과시킨다. 정수인지는 StrictIntegerDeserializer가 본다. */
    private static void validateProcessingPerMinute(Integer processingPerMinute) {
        if (processingPerMinute != null && processingPerMinute < 1) {
            throw new InvalidRegistrationException("분당 입장 인원은 1 이상의 정수여야 합니다.");
        }
    }
}
