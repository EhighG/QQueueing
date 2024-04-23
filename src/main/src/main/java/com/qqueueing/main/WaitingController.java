package com.practice.apiserver;

import com.practice.apiserver.sse.WaitingInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


//@Slf4j
@RequestMapping("/waiting")
@RestController
@RequiredArgsConstructor
public class WaitingController {

    private final WaitingService waitingService;

    @GetMapping
    public ResponseEntity<Long> enter(HttpServletRequest request) {
        // 요청이 프록시 등을 거쳐 넘어왔을 때, 원래 클라이언트의 ip주소를 담는 헤더
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null) {
            ip = request.getRemoteAddr(); // 프록시 안 거쳤을 때
        }

        return ResponseEntity
                .ok(waitingService.enter(ip));
    }

    @GetMapping("/{userIdx}")
    public ResponseEntity<WaitingInfo> getMyOrder(HttpServletResponse response,
                                                  @PathVariable Long userIdx) {
        return ResponseEntity
                .ok(waitingService.getMyOrder(response, userIdx));
    }

    @GetMapping("/{userIdx}/out")
    public ResponseEntity<Void> out(@PathVariable Long userIdx) {
        waitingService.out(userIdx);
        return ResponseEntity
                .ok()
                .build();
    }
}
