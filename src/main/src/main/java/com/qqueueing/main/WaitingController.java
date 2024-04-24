package com.qqueueing.main;

import com.qqueueing.main.model.TestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


//@Slf4j
@RequestMapping("/waiting")
@RestController
@RequiredArgsConstructor
public class WaitingController {

    private final WaitingService waitingService;
    private static Long ipVal = 1L;

    @GetMapping
    public ResponseEntity<TestDto> enter(HttpServletRequest request) {
        // 요청이 프록시 등을 거쳐 넘어왔을 때, 원래 클라이언트의 ip주소를 담는 헤더
//        String ip = request.getHeader("X-Forwarded-For");
//        if (ip == null) {
//            ip = request.getRemoteAddr(); // 프록시 안 거쳤을 때
//        }
        return ResponseEntity
                .ok(waitingService.enter("tmpUserId" + ipVal++));
    }

    @PostMapping("/{waitingIdx}")
    public ResponseEntity<Long> getMyOrder(HttpServletResponse response,
                                           @PathVariable Long waitingIdx,
                                           @RequestBody String idVal) {
        return ResponseEntity
                .ok(waitingService.getMyOrder(response, waitingIdx, idVal));
    }

    @GetMapping("/{waitingIdx}/out")
    public ResponseEntity<Void> out(@PathVariable Long waitingIdx) {
        waitingService.out(waitingIdx);
        return ResponseEntity
                .ok()
                .build();
    }
}
