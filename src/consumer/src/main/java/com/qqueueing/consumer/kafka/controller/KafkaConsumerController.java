package com.qqueueing.consumer.kafka.controller;

import com.qqueueing.consumer.kafka.dto.ConsumeMessageResDto;
import com.qqueueing.consumer.kafka.service.KafkaConsumerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/test")
@RestController
public class KafkaConsumerController {

    private final KafkaConsumerService kafkaConsumerService;

    @PostMapping
    public ConsumeMessageResDto test() {

//        kafkaConsumerService.consume("hi");
        ConsumeMessageResDto result = kafkaConsumerService.consumeMessages();

        return result;
    }
}
