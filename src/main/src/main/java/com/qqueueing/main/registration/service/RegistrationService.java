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

    public RegistrationService(RegistrationRepository registrationRepository) {
        this.registrationRepository = registrationRepository;
    }

    public Registration createRegistration(Registration registration) {
        return registrationRepository.save(registration);
    }

    public List<Registration> getAllRegistrations() {
        return registrationRepository.findAll();
    }
}
