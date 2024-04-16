package com.practice.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

//@Slf4j
@Component
@RequiredArgsConstructor
public class MsgConsumer {


    @KafkaListener(topics = "${kafka.topic.name}")
    public void consumeMsg(@Payload String s,
                           @Header(name = KafkaHeaders.RECEIVED_KEY, required = false) Long key,
                           @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                           @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                           @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long ts) {
         try {
             System.out.println("----------------consumed message------------------");
             System.out.println("key = " + key);
             System.out.println("partition = " + partition);
             System.out.println("topic = " + topic);
             System.out.println("ts = " + ts);
             System.out.println("message : " + s);
             System.out.println("----------------------------------");
        } catch (Exception e) {
             e.printStackTrace();
             throw new RuntimeException(e);
        }
    }
}
