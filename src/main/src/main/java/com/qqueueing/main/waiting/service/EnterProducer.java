package com.qqueueing.main.waiting.service;

import com.qqueueing.main.waiting.model.TestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicLong;

//@Slf4j
@Transactional
@Component
public class EnterProducer {

    private final KafkaTemplate<Long, String> kafkaTemplate;
    private final String TOPIC_NAME;
    private static AtomicLong id = new AtomicLong(1L);

    public EnterProducer(KafkaTemplate<Long, String> enterMsgTemplate,
                         @Value("${kafka.topic-names.enter}") String topicName) {
        this.kafkaTemplate = enterMsgTemplate;
        this.TOPIC_NAME = topicName;
    }

    public TestDto send(String message, int partitionNo) {
        Long curIdx = id.getAndIncrement();
        kafkaTemplate.send(TOPIC_NAME, message + curIdx);
        return new TestDto(curIdx, message + curIdx);
    }
}

