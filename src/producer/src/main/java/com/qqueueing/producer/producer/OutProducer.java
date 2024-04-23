package com.qqueueing.producer.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

//@Slf4j
@Component
public class OutProducer {

    private final KafkaTemplate<Long, Long> kafkaTemplate;

    private final String outTopic;
    private static AtomicLong id = new AtomicLong(1L);

    public OutProducer(KafkaTemplate<Long, Long> outMsgTemplate,
                       @Value("${kafka.topic-names.out}") String outTopic) {
        this.kafkaTemplate = outMsgTemplate;
        this.outTopic = outTopic;
    }

    public void send(Long enterTopicKey) {
        kafkaTemplate.send(outTopic, id.getAndIncrement(), enterTopicKey);
    }
}

