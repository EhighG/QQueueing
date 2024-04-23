package com.qqueueing.consumer.kafka.service;

import com.qqueueing.consumer.kafka.dto.ConsumeMessageResDto;
import com.qqueueing.consumer.kafka.dto.MessageDto;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;


@Slf4j
@Service
public class KafkaConsumerService {

//    @KafkaListener(topics = "test", groupId = "consumer_group01")
//    public void consume(String message) {
//        System.out.printf("Consumed Message : %s%n", message);
//    }

    @Value("${spring.kafka.consumer.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;

    @Value("${spring.kafka.consumer.key-deserializer}")
    private String keyDeserializer;

    @Value("${spring.kafka.consumer.value-deserializer}")
    private String valueDeserializer;

    @Value("${spring.kafka.consumer.topic}")
    private String topic;

    private KafkaConsumer<String, String> consumer;

    @PostConstruct
    public void init() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 10000);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 3);

        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topic));
    }

    public ConsumeMessageResDto consumeMessages() {

        ConsumeMessageResDto consumeMessageResDto = new ConsumeMessageResDto(new ArrayList<>());

        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));

        for (ConsumerRecord<String, String> record : records) {
            log.info("record:{}", record);
            MessageDto messageDto = new MessageDto(record.key(), record.value());
            consumeMessageResDto.getResult().add(messageDto);
        }


        System.out.println("result = " + consumeMessageResDto.getResult().toString());

        return consumeMessageResDto;

    }
}
