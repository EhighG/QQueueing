package com.qqueueing.consumer.kafka.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qqueueing.consumer.kafka.dto.*;
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

import java.sql.SQLOutput;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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

    @Value("${spring.kafka.consumer.max-poll-records}")
    private Integer maxPollRecords;

    private Map<Integer, Long> initialOffsets = new HashMap<>();

    private Map<Integer, KafkaConsumer<String, String>> consumers = new ConcurrentHashMap<>();

//    @PostConstruct
    public Properties init(int partitionNumber) {

        System.out.println("init!");

//        Runtime.getRuntime().addShutdownHook(new ShutdownThread());
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 10000);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);

//        consumer = new KafkaConsumer<>(props);
//        consumer.subscribe(Collections.singletonList(topic), new RebalanceListener()); // 모든 파티션을 읽어온다.

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        List<TopicPartition> partitions = new ArrayList<>();
        partitions.add(new TopicPartition(topic, partitionNumber));

        consumer.assign(partitions);

        consumers.put(partitionNumber, consumer);

        setInitialOffset(partitionNumber, consumer);

        return props;
    }

    private void setInitialOffset(int partitionNumber, KafkaConsumer<String, String> consumer) {
        // find initial offset
        PartitionInfo partitionInfo = consumer.partitionsFor(topic).stream()
                .filter(pi -> pi.partition() == partitionNumber)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("사용중이 아닌 파티션입니다."));

        TopicPartition topicPartition = new TopicPartition(partitionInfo.topic(),
                partitionInfo.partition());

        initialOffsets.put(partitionNumber, consumer.endOffsets(Collections.singleton(topicPartition)).get(topicPartition));
    }


    public void closeConsumer(int partitionNumber) {
        KafkaConsumer<String, String> consumer = consumers.get(partitionNumber);
        if (consumer != null) {
            consumer.close();
        }
    }


    public void deleteDatasOfPartition(int partitionNumber) {
        try {
            // AdminClient 생성
            AdminClient adminClient = AdminClient.create(init(partitionNumber));

            // 삭제할 토픽과 파티션 지정
            TopicPartition topicPartition = new TopicPartition(topic, partitionNumber);

            KafkaConsumer<String, String> consumer = consumers.get(partitionNumber);
            Long lastOffset = consumer.endOffsets(Collections.singleton(topicPartition)).get(topicPartition);
            System.out.println("마지막 offset : " + lastOffset);

            // 해당 파티션의 모든 레코드 삭제
            Map<TopicPartition, RecordsToDelete> recordsToDelete = Collections.singletonMap(topicPartition, RecordsToDelete.beforeOffset(lastOffset));
            DeleteRecordsResult deleteRecordsResult = adminClient.deleteRecords(recordsToDelete);

            // 삭제 작업 완료 대기
            deleteRecordsResult.all().get();

//            consumer.seek(topicPartition, 0L);
            consumer.seekToBeginning(Collections.singletonList(topicPartition));

            for(int i=0; i<10; i++) {
                System.out.println("poll 작동");
                consumer.poll(Duration.ofMillis(100));
            }

            consumer.commitSync();
            System.out.println("현재 파티션의 오프셋 : " + consumer.position(topicPartition));

            Long lastOffset2 = consumer.endOffsets(Collections.singleton(topicPartition)).get(topicPartition);
            System.out.println("마지막 offset이 0으로 초기화됨: " + lastOffset2);

            System.out.println("현재 할당된 파티션 : " + consumer.assignment());
            System.out.println("현재 파티션의 오프셋 : " + consumer.position(topicPartition));

//            consumer.assign(Collections.singletonList(topicPartition));
//            consumer.seek(topicPartition, lastOffset);
//
//            consumer.commitSync();
//
//            Long lastOffset2 = consumer.endOffsets(Collections.singleton(topicPartition)).get(topicPartition);
//            System.out.println("찐 마지막 offset : " + lastOffset2);

//            consumer.seekToBeginning(Collections.singletonList(topicPartition));

            System.out.println("All records in partition " + partitionNumber + " of topic " + topic + " deleted successfully.");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public void changeConsumerProperties(ChangeConsumerPropertiesReqDto changeConsumerPropertiesReqDto) {
        KafkaConsumer<String, String> consumer = consumers.get(changeConsumerPropertiesReqDto.getPartitionNumber());

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

    public synchronized Map<Integer, ConsumeMessageResDto> consumeMessages(List<Integer> partitionNumbers) {
        Map<Integer, ConsumeMessageResDto> resMap = new HashMap<>();
        for (Integer partitionNumber : partitionNumbers) {
            KafkaConsumer<String, String> consumer = consumers.get(partitionNumber);

            // Get partition information for the topic
            List<PartitionInfo> partitionInfos =
                    consumer.partitionsFor(topic);
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

            // Get the partition - partitions is not sequential
            PartitionInfo partitionInfo = partitionInfos.stream()
                    .filter(pi -> pi.partition() == partitionNumber)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("사용중이 아닌 파티션입니다."));

            // Create a TopicPartition object
            TopicPartition topicPartition = new TopicPartition(partitionInfo.topic(),
                    partitionInfo.partition());

            // Get the current offset for the partition
            Long currentOffset = consumer.position(topicPartition);
//            Long currentOffset = consumer.endOffsets(Collections.singleton(topicPartition)).get(topicPartition);


            ConsumeMessageResDto consumeMessageResDto = new ConsumeMessageResDto();
            consumeMessageResDto.setCurDoneList(new ArrayList<>());

            try {
                System.out.println("before poll");
                System.out.println("consumer.is = " + consumer.listTopics());
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

                System.out.println("after poll");
                Long lastOffset = consumer.endOffsets(Collections.singleton(topicPartition)).get(topicPartition);

                for (ConsumerRecord<String, String> record : records) {
                    log.info("record:{}", record);
                    MessageDto messageDto = new MessageDto(record.value());
                    log.info("messageDto = {}", messageDto);
                    consumeMessageResDto.getCurDoneList().add(messageDto.getIp());
                }
                Long initialOffset = initialOffsets.get(partitionNumber);
                System.out.println("partitionNumber = " + partitionNumber);
                System.out.println("initialOffset = " + initialOffset);
                System.out.println("currentOffset = " + currentOffset);
//                consumeMessageResDto.setTotalQueueSize(lastOffset - currentOffset);
//                consumeMessageResDto.setCurrentOffset(currentOffset);
                consumeMessageResDto.setLastOffset(lastOffset);
                consumeMessageResDto.setCurrentOffset(currentOffset - initialOffset);

            } catch (WakeupException e) {
                e.printStackTrace();
                log.info("Wakeup Consumer");
            } finally {
                log.info("Consumer close");
            }
            resMap.put(partitionNumber, consumeMessageResDto);
        }
        return resMap;
    }

//    class ShutdownThread extends Thread {
//
//        public void run() {
//            log.info("Shutdown hook");
//            consumer.wakeup();
//        }
//    }

}
