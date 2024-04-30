package com.qqueueing.main.registration.controller;

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
    public ResponseEntity<Registration> createRegistration(@RequestBody Registration registration) {
        Registration createdRegistration = registrationService.createRegistration(registration);
        return new ResponseEntity<>(createdRegistration, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Registration>> getAllRegistrations() {
        List<Registration> registrations = registrationService.getAllRegistrations();
        return new ResponseEntity<>(registrations, HttpStatus.OK);
    }

}
