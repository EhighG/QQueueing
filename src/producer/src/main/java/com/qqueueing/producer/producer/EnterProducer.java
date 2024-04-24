package com.qqueueing.producer.producer;

import com.qqueueing.producer.model.WaitingDto;
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
    private final String enterTopic;
    private static AtomicLong id = new AtomicLong(1L);

    public EnterProducer(KafkaTemplate<Long, String> enterMsgTemplate,
                         @Value("${kafka.topic-names.enter}") String enterTopic) {
        this.kafkaTemplate = enterMsgTemplate;
        this.enterTopic = enterTopic;
    }

    public Long send(String message) {
        Long curIdx = id.getAndIncrement();
//        WaitingDto waitingDto = new WaitingDto(id.getAndIncrement(), message);
        kafkaTemplate.send(enterTopic, message);
        return curIdx;
    }
}

