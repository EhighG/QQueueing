package com.qqueueing.main.registration.service;

import com.qqueueing.main.registration.model.Registration;
import com.qqueueing.main.registration.repository.RegistrationRepository;
import com.qqueueing.main.waiting.service.WaitingService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegistrationService {

//    @Autowired
    private RegistrationRepository registrationRepository;
    private final WaitingService waitingService;

    public RegistrationService(RegistrationRepository registrationRepository, WaitingService waitingService) {
        this.registrationRepository = registrationRepository;
        this.waitingService = waitingService;
    }

    public Registration createRegistration(Registration registration) {
        Registration savedRegistration = registrationRepository.save(registration);
        waitingService.activate(registration);
        return savedRegistration;
    }

    public List<Registration> getAllRegistrations() {
        return registrationRepository.findAll();
    }
}
