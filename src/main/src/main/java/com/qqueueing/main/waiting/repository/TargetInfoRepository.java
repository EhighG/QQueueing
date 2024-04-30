package com.qqueueing.main.waiting.repository;

import com.qqueueing.main.waiting.model.entity.TargetInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TargetInfoRepository extends MongoRepository<TargetInfo, String>, CustomizedTargetInfoRepository {
}
