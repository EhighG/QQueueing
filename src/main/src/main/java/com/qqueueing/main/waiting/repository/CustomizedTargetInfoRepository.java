package com.qqueueing.main.waiting.repository;

import com.qqueueing.main.waiting.model.entity.TargetInfo;

public interface CustomizedTargetInfoRepository {
    TargetInfo findByTopicName(String topicName);
    void deleteByTopicName(String topicName);
}
