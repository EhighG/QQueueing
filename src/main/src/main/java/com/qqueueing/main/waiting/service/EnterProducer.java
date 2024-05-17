package com.qqueueing.main.waiting.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qqueueing.main.waiting.model.EnterQueueResDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

@Slf4j
@Transactional
@Component
public class EnterProducer {

    private final KafkaTemplate<Long, String> kafkaTemplate;
    private final String TOPIC_NAME;
    private static Map<Integer, AtomicLong> ids;
    private ObjectMapper mapper = new ObjectMapper();

    public EnterProducer(KafkaTemplate<Long, String> enterMsgTemplate,
                         @Value("${kafka.topics.enter.name}") String topicName) {
        log.info("Start -- EnterProducer constructor");
        this.kafkaTemplate = enterMsgTemplate;
        this.TOPIC_NAME = topicName;
        this.ids = new HashMap<>();
//        initialize();
        log.info("End -- EnterProducer constructor");
    }

    public void initialize() {
        log.info("Start -- EnterProducer.initialize()");
        IntStream.range(0, 20)
                .forEach(num -> {
                    CompletableFuture<SendResult<Long, String>> sendResult = kafkaTemplate.send(TOPIC_NAME, num, (long) num, "re-init");
                    sendResult.whenComplete((response, exception) -> {
                        if (exception != null) {
                            exception.printStackTrace();
                            System.out.println("error!");
                        }
                        if (response != null) {
                            log.info("response = {}", response);
                        }
                    });
                    log.info("while enterProducer.initialize... init partition {}", num);
                });
        log.info("End -- EnterProducer.initialize()");
    }

    public void activate(int partitionNo) {
        ids.put(partitionNo, new AtomicLong(1L));
    }

    public EnterQueueResDto send(String message, long key, int partitionNo) {
        AtomicLong id = ids.get(partitionNo);

        if(id == null) {
            id = new AtomicLong();
        }
        Long curIdx = id.getAndIncrement();
        String sampleIp = message + curIdx;

//        log.info("기본 토픽 : " + kafkaTemplate.getDefaultTopic());

        // 파티션 번호를 키로 사용하여 메시지 보냄
        kafkaTemplate.send(TOPIC_NAME, partitionNo, key, sampleIp);
        log.info("-------------------------------------- produced. idx = {} ----------------------------", curIdx);
        return new EnterQueueResDto(partitionNo, curIdx, sampleIp);
    }

    public long getLastEnteredIdx(int partitionNo) {
        return ids.get(partitionNo).get() - 1;
    }
}

