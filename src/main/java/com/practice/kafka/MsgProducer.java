package com.practice.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

//@Slf4j
@Component
@RequiredArgsConstructor
public class MsgProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final String topicName = "tests";
    private static Long id = 0L;

    public void send(String message) {
        kafkaTemplate.send(topicName, ++id + "", message);
        System.out.println("sending finished");
    }
}
