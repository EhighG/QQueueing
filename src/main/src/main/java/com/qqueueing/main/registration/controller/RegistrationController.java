package com.qqueueing.main.registration.controller;

import com.qqueueing.main.common.SuccessResponse;
import com.qqueueing.main.registration.model.Registration;
import com.qqueueing.main.registration.service.RegistrationService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/queue")
@RestController
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @PostMapping
    public ResponseEntity<?> createRegistration(@RequestBody Registration registration) {
        Registration createdRegistration = registrationService.createRegistration(registration);
        String message = "등록되었습니다.";
        SuccessResponse response = new SuccessResponse(HttpStatus.CREATED.value(), message, createdRegistration);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<?> getAllRegistrations() {
        List<Registration> registrations = registrationService.getAllRegistrations();
        String message = "조회에 성공했습니다.";
        SuccessResponse response = new SuccessResponse(HttpStatus.OK.value(), message, registrations);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
