package com.practice.producerserver.controller;

import com.practice.producerserver.producer.EnterProducer;
import com.practice.producerserver.producer.OutProducer;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/waiting")
public class WaitingController {

    private final EnterProducer enterProducer;
    private final OutProducer outProducer;

    public WaitingController(EnterProducer enterProducer, OutProducer outProducer) {
        this.enterProducer = enterProducer;
        this.outProducer = outProducer;
    }

    @PostMapping
    public Long enter(@RequestBody String clientIp) {
        return enterProducer.send(clientIp);
    }

    @GetMapping("/{enterTopicKey}/out")
    public void out(@PathVariable Long enterTopicKey) {
        outProducer.send(enterTopicKey);
    }
}

