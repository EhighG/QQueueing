package com.qqueueing.consumer.kafka.service;

import com.qqueueing.consumer.kafka.dto.ChangeConsumerPropertiesReqDto;
import com.qqueueing.consumer.kafka.dto.ConsumeMessageResDto;
import com.qqueueing.consumer.kafka.dto.MessageDto;
import com.qqueueing.consumer.kafka.listener.RebalanceListener;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;


@Slf4j
@Service
public class KafkaConsumerService {

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
    public Properties init() {

        System.out.println("init!");

//        Runtime.getRuntime().addShutdownHook(new ShutdownThread());
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 10000);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 2000);

//        consumer = new KafkaConsumer<>(props);
//        consumer.subscribe(Collections.singletonList(topic), new RebalanceListener());

        consumer = new KafkaConsumer<>(props);
        List<TopicPartition> partitions = new ArrayList<>();
        int partitionNumber = 0;
        partitions.add(new TopicPartition(topic, partitionNumber));

        consumer.assign(partitions);

        return props;
    }


    public void deleteDatasOfPartition() {

        try {

            AdminClient adminClient = AdminClient.create(init());

            TopicPartition topicPartition = new TopicPartition(topic, 0); // 삭제하려는 토픽과 파티션 지정

            // 삭제할 레코드의 오프셋을 파티션의 시작 오프셋으로 지정하여 모든 레코드 삭제
            long offset = 0;

            // 레코드 삭제 실행
            DeleteRecordsResult deleteRecordsResult = adminClient.deleteRecords(
                    Collections.singletonMap(topicPartition, RecordsToDelete.beforeOffset(offset)));

            // 결과 확인
            KafkaFuture<Void> future = deleteRecordsResult.all();
            future.get(); // 삭제 작업 완료를 기다림
            System.out.println("All records before offset " + offset + " deleted successfully.");

        } catch (InterruptedException | ExecutionException e) {

            System.out.println(e.getMessage());
        }
    }

    public void changeConsumerProperties(ChangeConsumerPropertiesReqDto changeConsumerPropertiesReqDto) {

        consumer.close();

        Properties newProps = new Properties();
        newProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        newProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        newProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        newProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        newProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
        newProps.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 10000);
        newProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, changeConsumerPropertiesReqDto.getMaxPollRecordsConfig());

        consumer = new KafkaConsumer<>(newProps);
        consumer.subscribe(Collections.singletonList(topic), new RebalanceListener());
    }

    public synchronized ConsumeMessageResDto consumeMessages() {

        // Kafka partition number (zero-based index)
        int partitionNumber = 0;

        // Get partition information for the topic
        List<PartitionInfo> partitionInfos = consumer.partitionsFor(topic);
        System.out.println("파티션 정보 : " + partitionInfos.toString());

        // Check if the requested partition number is valid
        if (consumer.assignment().isEmpty()) {
            log.error("No partitions assigned to the consumer. Trying to reassign partitions...");
            consumer.subscribe(Collections.singletonList(topic), new RebalanceListener());
            // Try to reassign partitions
            if (consumer.assignment().isEmpty()) {
                log.error("Failed to assign partitions. Cannot consume messages.");
//                return null;
            }
        }

        // Get the partition
        PartitionInfo partitionInfo = partitionInfos.get(partitionNumber);

        // Create a TopicPartition object
        TopicPartition topicPartition = new TopicPartition(partitionInfo.topic(),
                partitionInfo.partition());

        // Get the current offset for the partition
        Long currentOffset = consumer.position(topicPartition);

        Long lastOffset = consumer.endOffsets(Collections.singleton(topicPartition)).get(topicPartition);

        ConsumeMessageResDto consumeMessageResDto = new ConsumeMessageResDto();
        consumeMessageResDto.setCurDoneSet(new ArrayList<>());
        consumeMessageResDto.setBatchLastIdx(currentOffset);
        consumeMessageResDto.setTotalQueueSize(lastOffset - currentOffset);

        try {

            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));

            for (ConsumerRecord<String, String> record : records) {
                log.info("record:{}", record);
//                MessageDto messageDto = new MessageDto(record.value());
                consumeMessageResDto.getCurDoneSet().add(record.value());
            }

        } catch (WakeupException e) {
            log.info("Wakeup Consumer");
        } finally {
            log.info("Consumer close");
//            consumer.close();
        }



        return consumeMessageResDto;
    }

//    class ShutdownThread extends Thread {
//
//        public void run() {
//            log.info("Shutdown hook");
//            consumer.wakeup();
//        }
//    }

}
