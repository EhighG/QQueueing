package com.qqueueing.main.registration.service;

import com.qqueueing.main.registration.model.Registration;
import com.qqueueing.main.registration.repository.RegistrationRepository;
import com.qqueueing.main.waiting.service.WaitingService;
import org.springframework.data.crossstore.ChangeSetPersister;
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

    public Registration getRegistrationById(String id) throws ChangeSetPersister.NotFoundException {
        return registrationRepository.findById(id)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());
    }

//    public Registration updateRegistrationById(String id, RegistrationUpdateRequest request) throws ChangeSetPersister.NotFoundException {
//        Registration registration = registrationRepository.findById(id)
//                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());
//        registration.update(request.getName(), request.getEmail(), request.getPhoneNumber());
//        return registrationRepository.save(registration);
//    }
//    public Registration createRegistration(Registration registration) {
//        return registrationRepository.save(registration);
//    }
//    public Registration createRegistration(Registration registration) {
//        return registrationRepository.save(registration);
//    }

}
