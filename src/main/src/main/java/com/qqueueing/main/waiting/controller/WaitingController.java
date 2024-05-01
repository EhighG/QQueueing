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
    public ResponseEntity<?> enter(HttpServletRequest request) {
        try {
            return ResponseEntity
                    .ok(waitingService.enter(request));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e);
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

    @GetMapping("/{waitingIdx}/out")
    public ResponseEntity<Void> out(@RequestParam int partitionNo,
                                    @PathVariable Long waitingIdx) {
        waitingService.out(partitionNo, waitingIdx);
        return ResponseEntity
                .ok()
                .build();
    }

    @PostMapping("/{partitionNo}/activate")
    public ResponseEntity<?> activateQueue(@PathVariable("partitionNo") int partitionNo) {
        waitingService.activate(partitionNo);
        return ResponseEntity
                .ok()
                .build();
    }

    @PostMapping("/{partitionNo}/deactivate")
    public ResponseEntity<?> deactivateQueue(@PathVariable("partitionNo") int partitionNo) {
        waitingService.deactivate(partitionNo);
        return ResponseEntity
                .ok()
                .build();
    }

    @GetMapping
    public String forwardingTest() {
       return "forwarding success!";
    }
}
