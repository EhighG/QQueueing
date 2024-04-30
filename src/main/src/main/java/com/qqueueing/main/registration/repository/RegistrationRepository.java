package com.qqueueing.main.registration.repository;

import com.qqueueing.main.registration.model.Registration;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationRepository extends MongoRepository<Registration, String> {

}
