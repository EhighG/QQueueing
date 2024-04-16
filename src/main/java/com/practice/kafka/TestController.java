package com.practice.kafka;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


//@Slf4j
@RestController
@RequiredArgsConstructor
public class TestController {

    private final MsgProducer producer;

    @GetMapping("/test")
    public String test(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null) {
            ip = request.getRemoteAddr();
        }

        producer.send(ip);
        return "go to waiting page";
    }
}
