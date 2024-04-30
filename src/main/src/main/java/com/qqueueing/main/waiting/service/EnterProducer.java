package com.qqueueing.main.waiting.service;

import com.qqueueing.main.waiting.model.TestDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicLong;

//@Slf4j
@Transactional
@Component
public class EnterProducer {

    private final KafkaTemplate<Long, String> kafkaTemplate;
    private static AtomicLong id = new AtomicLong(1L);

    public EnterProducer(KafkaTemplate<Long, String> enterMsgTemplate) {
        this.kafkaTemplate = enterMsgTemplate;
    }

    public TestDto send(String topicName, String message) {
        Long curIdx = id.getAndIncrement();
        kafkaTemplate.send(topicName, message + curIdx);
        return new TestDto(curIdx, message + curIdx);
    }
}

