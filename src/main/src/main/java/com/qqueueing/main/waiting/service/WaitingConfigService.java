package com.qqueueing.main.waiting.service;

import com.qqueueing.main.waiting.model.QueueInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Slf4j
@Service
public class WaitingConfigService {
    private final KafkaTopicManager kafkaTopicManager;

    public WaitingConfigService(KafkaTopicManager kafkaTopicManager) {
        this.kafkaTopicManager = kafkaTopicManager;
    }

    public void queueOn(String topicName) {
        QueueInfoDto queueInfoDto = findByTopicName(topicName);
        if (queueInfoDto == null) {
            kafkaTopicManager.createTopic(topicName);
            saveQueueInfo(new QueueInfoDto(topicName));
        }
        else if (queueInfoDto.isActive()) {
            log.error("대기열이 이미 켜져 있습니다.");
        } else { // exist, not active
            kafkaTopicManager.deleteTopic(topicName);
            kafkaTopicManager.createTopic(topicName);
            updateQueueStatus(queueInfoDto, true);
        }
    }

    public void queueOff(String topicName) {
        QueueInfoDto queueInfoDto = findByTopicName(topicName);
        if (queueInfoDto == null) {
            log.error("대기열이 존재하지 않습니다.");
        } else if (!queueInfoDto.isActive()){
            log.error("대기열이 이미 꺼져 있습니다.");
        } else {
            updateQueueStatus(queueInfoDto, false);
        }
    }

    private void updateQueueStatus(QueueInfoDto queueInfoDto, boolean isActive) {

    }

    private void saveQueueInfo(QueueInfoDto queueInfoDto) {
        // db에 대기열 정보 저장
    }

    private QueueInfoDto findByTopicName(String topicName) {
        // db에서 대기열 대상 정보를 꺼내옴.
        return new QueueInfoDto("empty");
    }
}

