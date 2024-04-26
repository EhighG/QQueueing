package com.qqueueing.consumer.kafka.controller;

import com.qqueueing.consumer.common.response.SuccessResponse;
import com.qqueueing.consumer.kafka.dto.ChangeConsumerPropertiesReqDto;
import com.qqueueing.consumer.kafka.dto.ConsumeMessageResDto;
import com.qqueueing.consumer.kafka.service.KafkaConsumerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/consume")
@RestController
public class KafkaConsumerController {

    private final KafkaConsumerService kafkaConsumerService;

    @PostMapping
    public ResponseEntity<?> consumeMessage() {

        ConsumeMessageResDto result = kafkaConsumerService.consumeMessages();

        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK.value(), "메시지를 consume하였습니다.", result));
    }

    @PostMapping("/change")
    public ResponseEntity<?> changeConsumerProperties(@RequestBody ChangeConsumerPropertiesReqDto changeConsumerPropertiesReqDto) {

        kafkaConsumerService.changeConsumerProperties(changeConsumerPropertiesReqDto);

        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK.value(), "가져올 데이터의 개수를 변경하였습니다.", null));
    }
}
