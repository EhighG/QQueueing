package com.qqueueing.main.waiting.controller;


import com.qqueueing.main.common.SuccessResponse;
import com.qqueueing.main.waiting.model.GetMyOrderReqDto;
import com.qqueueing.main.waiting.model.GetMyOrderResDto;
import com.qqueueing.main.waiting.service.WaitingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;


@Slf4j
@RequestMapping("/waiting")
@RestController
public class WaitingController {

    private final WaitingService waitingService;
    @Value("${servers.front}")
    private String frontUrl;

    public WaitingController(WaitingService waitingService) {
        this.waitingService = waitingService;
    }

    @PostMapping
    public ResponseEntity<?> enter(HttpServletRequest request, HttpServletResponse response) {
        Object result = waitingService.enter(request);
        if (result != null) {
            return ResponseEntity
                    .ok(new SuccessResponse(HttpStatus.OK.value(), "대기열에 입장되었습니다.", result));
        }
        try {
            // 대기열 비활성화 시 -> redirect
            return ResponseEntity
                    .status(302)
                    .location(new URI(frontUrl))
                    .build();
        } catch (URISyntaxException e) {
            log.error(e.getMessage());
            throw new RuntimeException("리다이렉트 설정 중 에러");
        }
    }


    @PostMapping("/order")
    public ResponseEntity<?> getMyOrder(@RequestBody GetMyOrderReqDto getMyOrderReqDto,
                                        HttpServletRequest request) {
        Object myOrderRes = waitingService.getMyOrder(getMyOrderReqDto.getPartitionNo(),
                getMyOrderReqDto.getOrder(),
                getMyOrderReqDto.getIdVal(),
                request);
        if (myOrderRes instanceof GetMyOrderResDto) {
            return ResponseEntity
                    .ok(myOrderRes);
        } else {
            return (ResponseEntity<?>) myOrderRes;
        }
    }

    @GetMapping("/out")
    public ResponseEntity<Void> out(@RequestParam int partitionNo,
                                    @RequestParam Long order) {
        waitingService.out(partitionNo, order);
        return ResponseEntity
                .ok()
                .build();
    }

    @PostMapping("/{partitionNo}/activate")
    public ResponseEntity<?> activateQueue(@PathVariable("partitionNo") int partitionNo) {
        waitingService.activate(partitionNo);
        return ResponseEntity
                .ok(new SuccessResponse(HttpStatus.OK.value(), "활성화되었습니다."));
    }

    @PostMapping("/{partitionNo}/deactivate")
    public ResponseEntity<?> deactivateQueue(@PathVariable("partitionNo") int partitionNo) {
        waitingService.deactivate(partitionNo);
        return ResponseEntity
                .ok(new SuccessResponse(HttpStatus.OK.value(), "비활성화되었습니다."));
    }

    @GetMapping
    public String forwardingTest() {
       return "forwarding success!";
    }

    // 여러 대기열 테스트용 메소드들

    @PostMapping("/testlink2")
    public ResponseEntity<?> enter2(HttpServletRequest request, HttpServletResponse response) {
        Object result = waitingService.enter(request);
        if (result != null) {
            return ResponseEntity
                    .ok(new SuccessResponse(HttpStatus.OK.value(), "대기열에 입장되었습니다.", result));
        }
        try {
            // 대기열 비활성화 시 -> redirect
            return ResponseEntity
                    .status(302)
                    .location(new URI(frontUrl))
                    .build();
        } catch (URISyntaxException e) {
            log.error(e.getMessage());
            throw new RuntimeException("리다이렉트 설정 중 에러");
        }
    }

    @GetMapping("/testlink2")
    public String forwardingTest2() {
        return "TestLink2 : forwardingdingding!";
    }
}
