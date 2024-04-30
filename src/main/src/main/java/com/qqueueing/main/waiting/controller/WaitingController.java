package com.qqueueing.main.waiting.controller;


import com.qqueueing.main.waiting.model.GetMyOrderReqDto;
import com.qqueueing.main.waiting.model.GetMyOrderResDto;
import com.qqueueing.main.waiting.service.WaitingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


//@Slf4j
@RequestMapping("/waiting")
@RestController
public class WaitingController {

    private final WaitingService waitingService;

    public WaitingController(WaitingService waitingService) {
        this.waitingService = waitingService;
    }

    @PostMapping
    public ResponseEntity<?> enter(String topicName, HttpServletRequest request) {
        try {
            return ResponseEntity
                    .ok(waitingService.enter(topicName, request));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e);
        }
    }

    @PostMapping("/order")
    public ResponseEntity<?> getMyOrder(@RequestBody GetMyOrderReqDto getMyOrderReqDto,
                                        HttpServletRequest request) {
        Object myOrderRes = waitingService.getMyOrder(getMyOrderReqDto.getTopicName(),
                getMyOrderReqDto.getWaitingIdx(),
                getMyOrderReqDto.getIdVal(),
                request);
        if (myOrderRes instanceof GetMyOrderResDto) {
            return ResponseEntity
                    .ok(myOrderRes);
        } else {
            return (ResponseEntity<?>) myOrderRes;
        }
    }

    @GetMapping("/{waitingIdx}/out")
    public ResponseEntity<Void> out(@RequestParam String topic,
                                    @PathVariable Long waitingIdx) {
        waitingService.out(topic, waitingIdx);
        return ResponseEntity
                .ok()
                .build();
    }

    @PostMapping("/{topicName}/activate")
    public ResponseEntity<?> activateQueue(@PathVariable String topicName) {
        waitingService.activate(topicName);
        return ResponseEntity
                .ok()
                .build();
    }

    @PostMapping("/{topicName}/deactivate")
    public ResponseEntity<?> deactivateQueue(@PathVariable String topicName) {
        waitingService.deactivate(topicName);
        return ResponseEntity
                .ok()
                .build();
    }
}
