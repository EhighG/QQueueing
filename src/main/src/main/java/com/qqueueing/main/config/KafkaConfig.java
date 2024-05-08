package com.qqueueing.main.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    private final String GROUP_ID = "Producer";
    private final String KAFKA_BROKER;

    public KafkaConfig(@Value("${kafka.broker}") String kafkaBrokerAddr) {
        KAFKA_BROKER = kafkaBrokerAddr;
    }

    @Bean
    public ProducerFactory<Long, String> longObjectProducerFactory() {
        return new DefaultKafkaProducerFactory<>(longObjectProducerConfig());
    }

    @Bean
    public Map<String, Object> longObjectProducerConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKER);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }

    @Bean
    public KafkaTemplate<Long, String> longObjectKafkaTemplate() {
        return new KafkaTemplate<Long, String>(longObjectProducerFactory());
    }

}
