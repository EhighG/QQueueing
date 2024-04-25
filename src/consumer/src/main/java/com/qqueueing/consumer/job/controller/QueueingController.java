package com.qqueueing.consumer.job.controller;

import com.qqueueing.consumer.common.response.SuccessResponse;
import com.qqueueing.consumer.job.dto.StartBatchReqDto;
import com.qqueueing.consumer.job.service.QueueingService;
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
@RequestMapping("/batch")
@RestController
public class QueueingController {

    private final QueueingService queingService;

    @PostMapping
    public ResponseEntity<?> startBatch(@RequestBody StartBatchReqDto startBatchReqDto) {

        queingService.startBatch(startBatchReqDto);

        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK.value(), "batch 실행 요청 성공", null));
    }


}
