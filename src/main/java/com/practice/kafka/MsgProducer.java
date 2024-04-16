package com.practice.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

//@Slf4j
@Component
@RequiredArgsConstructor
public class MsgProducer {

    private final KafkaTemplate<Long, String> kafkaTemplate;

    @Value("${kafka.topic.name}")
    private String topicName;
    private static AtomicLong id = new AtomicLong(1L);

    public void send(String message) {
        kafkaTemplate.send(topicName, id.getAndIncrement(), message);
    }
}
