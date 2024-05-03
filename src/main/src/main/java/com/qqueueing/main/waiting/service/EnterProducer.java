package com.qqueueing.main.waiting.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qqueueing.main.waiting.model.EnterQueueResDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

//@Slf4j
@Transactional
@Component
public class EnterProducer {

    private final KafkaTemplate<Long, String> kafkaTemplate;
    private final String TOPIC_NAME;
    private static Map<Integer, AtomicLong> ids;
    private ObjectMapper mapper = new ObjectMapper();

    public EnterProducer(KafkaTemplate<Long, String> enterMsgTemplate,
                         @Value("${kafka.topic-names.enter}") String topicName) {
        this.kafkaTemplate = enterMsgTemplate;
        this.TOPIC_NAME = topicName;
        this.ids = new HashMap<>();
    }

    public void activate(int partitionNo) {
        ids.put(partitionNo, new AtomicLong(1L));
    }

    public EnterQueueResDto send(String message, int partitionNo) {
        AtomicLong id = ids.get(partitionNo);
//        System.out.println("partitionNo = " + partitionNo);
//        System.out.println("id = " + id);
        Long curIdx = id.getAndIncrement();
        String sampleIp = message + curIdx;
        kafkaTemplate.send(TOPIC_NAME, partitionNo, null, sampleIp); // key null 잘 되는지 체크!!
//        EnterInfoDto enterInfoDto = new EnterInfoDto(curIdx, message);
//        try {
////            kafkaTemplate.send(TOPIC_NAME, partitionNo, null, mapper.writeValueAsString(enterInfoDto)); // key null 잘 되는지 체크!!
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
        return new EnterQueueResDto(partitionNo, curIdx, sampleIp);
//        return new EnterQueueResDto(partitionNo, curIdx);
    }
}

