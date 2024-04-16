package com.practice.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//@Slf4j
@RestController
@RequiredArgsConstructor
public class TestController {

    private final MsgConsumer consumer;
    private final MsgProducer producer;
    private static Integer id = 1;

    @GetMapping("/test")
    public String test(@RequestParam String msg) {
        producer.send(msg + id++);
        System.out.println("in controller / produced");

        return "ok";
    }
}
