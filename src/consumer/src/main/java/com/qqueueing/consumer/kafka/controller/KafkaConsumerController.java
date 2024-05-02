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

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/consume")
@RestController
public class KafkaConsumerController {

    private final KafkaConsumerService kafkaConsumerService;

    @PostMapping
    public Map<Integer, ConsumeMessageResDto> consumeMessage(@RequestBody List<Integer> partitionNumbers) {
        System.out.println("partitionNumbers = " + partitionNumbers);
//        ConsumeMessageResDto result = kafkaConsumerService.consumeMessages();
        Map<Integer, ConsumeMessageResDto> result = kafkaConsumerService.consumeMessages(partitionNumbers);
        System.out.println("result = " + result);
//        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK.value(), "메시지를 consume하였습니다.", result));
        return result;
    }

    @PostMapping("/start")
    public ResponseEntity<?> startQueueing(@RequestBody int partitionNumber) {

        System.out.println("start");
        kafkaConsumerService.deleteDatasOfPartition(partitionNumber);

        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK.value(), "대기열을 시작하였습니다. 새로운 partition이 할당되었습니다.", null));
    }

    @PostMapping("/close")
    public ResponseEntity<?> closeQueueing(@RequestBody int partitionNumber) {
        kafkaConsumerService.closeConsumer(partitionNumber);
        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK.value(), "대기열이 삭제되었습니다. 파티션은 대기열이 새로 시작할 때 초기화됩니다.", null));

    }

    @PostMapping("/change")
    public ResponseEntity<?> changeConsumerProperties(@RequestBody ChangeConsumerPropertiesReqDto changeConsumerPropertiesReqDto) {

        kafkaConsumerService.changeConsumerProperties(changeConsumerPropertiesReqDto);

        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK.value(), "가져올 데이터의 개수를 변경하였습니다.", null));
    }
}
