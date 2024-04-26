package com.qqueueing.consumer.kafka.config;
//
//import org.apache.kafka.clients.producer.ProducerConfig;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.annotation.EnableKafka;
//import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
//import org.springframework.kafka.core.ConsumerFactory;
//import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@EnableKafka
//@Configuration
//public class KafkaConsumerConfig {
//
//    @Value("${spring.kafka.consumer.bootstrap-servers}")
//    private String BOOTSTRAP_SERVER;
//
//    @Value("${spring.kafka.consumer.group-id}")
//    private String GROUP_ID;
//
//    @Value("${spring.kafka.consumer.auto-offset-reset}")
//    private String AUTO_OFFSET_RESET;
//
//
//    @Bean
//    public Map<String, Object> consumerConfigs() {
//        Map<String, Object> props = new HashMap<>();
//        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
//        props.put("group.id", GROUP_ID);
//        props.put("key.deserializer", "org.apache.kafka.common.serialization.LongDeserializer");
//        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
//        props.put("auto.offset.reset", AUTO_OFFSET_RESET);
//
//        return props;
//    }
//
//    @Bean
//    public ConsumerFactory<String, String> consumerFactory() {
//        return new DefaultKafkaConsumerFactory<>(this.consumerConfigs());
//    }
//
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String,String> kafkaListenerContainerFactory() {
//
//        ConcurrentKafkaListenerContainerFactory<String,String> factory
//                = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(this.consumerFactory());
//        return factory;
//    }
//
//}