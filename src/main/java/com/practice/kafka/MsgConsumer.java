package com.practice.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

//@Slf4j
@Component
@RequiredArgsConstructor
public class MsgConsumer {


    @KafkaListener(topics = "tests", groupId = "test01")
    public void consumeMsg(String s) {
        try {
            System.out.println("Consume the event {}" + s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
