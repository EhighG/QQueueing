package com.qqueueing.main.queue_regist_dummy;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/queue")
@RestController
public class DummyController {

    @PostMapping
    public ResponseEntity<?> register(@RequestBody DummyRegisterReqDto reqDto) {
        return ResponseEntity
                .ok(new SuccessResponse(HttpStatus.OK.value(), "등록되었습니다.",
                        DummyRegisterResDto.builder()
                                .id("663077970d2d9848ca352e4a")
                                .topicName("testTopic")
                                .targetUrl(reqDto.getTargetUrl())
                                .maxCapacity(reqDto.getMaxCapacity())
                                .processingPerMinute(reqDto.getProcessingPerMinute())
                                .serviceName(reqDto.getServiceName())
                                .queueImageUrl(reqDto.getQueueImageUrl())
                                .isActive(false)
                                .build()));
    }

    @GetMapping
    public ResponseEntity<?> findAll() {
        List<DummyRegisterResDto> result = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            result.add(DummyRegisterResDto.builder()
                    .id("663077970d2d9848ca352e4a")
                    .topicName("testTopic")
                    .targetUrl("http://targetUrl" + (i + 1))
                    .maxCapacity(2000)
                    .processingPerMinute(100)
                    .serviceName("testServiceName" + (i+1))
                    .queueImageUrl("testProductImageUrl")
                    .isActive(false)
                    .build());
        }
        return ResponseEntity
                .ok(new SuccessResponse(HttpStatus.OK.value(), "조회에 성공했습니다.", result));
    }
}
