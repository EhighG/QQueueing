package com.qqueueing.producer.controller;

import com.qqueueing.producer.producer.EnterProducer;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/waiting")
public class WaitingController {

    private final EnterProducer enterProducer;
//    private final OutProducer outProducer;

    public WaitingController(EnterProducer enterProducer) {
        this.enterProducer = enterProducer;
    }

    @PostMapping
    public Long enter(@RequestBody String clientIp) {
        return enterProducer.send(clientIp);
    }

//    @GetMapping("/{enterTopicKey}/out")
//    public void out(@PathVariable Long enterTopicKey) {
//    }
}

