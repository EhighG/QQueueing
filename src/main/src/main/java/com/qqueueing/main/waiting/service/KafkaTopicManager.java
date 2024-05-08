package com.qqueueing.main.waiting.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.DeleteTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class KafkaTopicManager {

    private final String KAFKA_BROKER;
    private final Integer PARTITION_NUM;
    private final short REPLICATION_FACTOR = (short) 1;

    public KafkaTopicManager(@Value("${kafka.broker}") String kafkaBrokerAddr,
                             @Value("${kafka.partition.max-index}") Integer partitoinNum) {
        KAFKA_BROKER = kafkaBrokerAddr;
        PARTITION_NUM = partitoinNum;
    }

    public Set<String> getTopics() {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKER);

        try (AdminClient adminClient = AdminClient.create(props)) {
            return adminClient.listTopics().names().get();
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage());
        }
        log.error("getTopics error!");
        return new HashSet<>();
    }

    public void createTopic(String topicName) {
        log.info("Start -- createTopic : topicName = {}", topicName);
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKER);

        try (AdminClient adminClient = AdminClient.create(props)) {
            NewTopic newTopic = new NewTopic(topicName, PARTITION_NUM, REPLICATION_FACTOR);
            adminClient.createTopics(Collections.singletonList(newTopic)).all().get();
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage());
        }
        log.info("End -- createTopic : topicName = {}", topicName);
    }

    public void deleteTopic(String topicName) {
        log.info("Start -- deleteTopic : topicName = {}", topicName);
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKER);

        try (AdminClient adminClient = AdminClient.create(props)) {
            DeleteTopicsResult deleteTopicsResult = adminClient.deleteTopics(Collections.singletonList(topicName));
            deleteTopicsResult.all().get();
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage());
        }
        log.info("End -- deleteTopic : topicName = {}", topicName);
    }
}
