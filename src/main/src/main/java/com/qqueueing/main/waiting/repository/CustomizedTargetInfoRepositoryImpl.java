package com.qqueueing.main.waiting.repository;

import com.qqueueing.main.waiting.model.entity.TargetInfo;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class CustomizedTargetInfoRepositoryImpl implements CustomizedTargetInfoRepository {

    private MongoTemplate mongoTemplate;

    public CustomizedTargetInfoRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public TargetInfo findByTopicName(String topicName) {
        Query query = new Query(Criteria.where("topic_name").is(topicName));
        return mongoTemplate.findOne(query, TargetInfo.class);
    }

    public void deleteByTopicName(String topicName) {
        return;
    }
}
