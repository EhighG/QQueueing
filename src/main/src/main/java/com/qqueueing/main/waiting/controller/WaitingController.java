package com.qqueueing.main.waiting.controller;


import com.qqueueing.main.common.SuccessResponse;
import com.qqueueing.main.waiting.model.EnqueueResponse;
import com.qqueueing.main.waiting.model.WaitingOrderRequest;
import com.qqueueing.main.waiting.model.WaitingOrderResponse;
import com.qqueueing.main.waiting.service.WaitingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.nio.charset.StandardCharsets;


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


    // for test
    @GetMapping("/endpoint")
    public ResponseEntity<?> changeEndPoint(@RequestParam(value = "endpoint", required = false) String endPoint) {
        waitingService.setEndpoint(endPoint);
        return ResponseEntity
                .ok()
                .build();
    }

    @GetMapping("/enter")
    public ResponseEntity<?> enter(HttpServletRequest request) {
        String targetUrl = request.getHeader("Target-URL");
        if (targetUrl == null) targetUrl = request.getRequestURL().toString();

        URI redirectUrl = waitingService.enter(targetUrl);
//        log.info("redirectUrl.toString() = {}", redirectUrl.toString());
        return ResponseEntity
                .status(302)
                .location(redirectUrl) // queue-page or target-page
                .build();
    }

    @GetMapping("/queue-page")
    public ResponseEntity<?> getQueuePage(@RequestParam(value = "Target-URL") String targetUrl) {
//        log.info("targetUrl = {}", targetUrl);
//        log.info("queue-page 포워딩 api called");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("text", "html", StandardCharsets.UTF_8));
//
        String result = waitingService.getQueuePage(targetUrl);
//
//        return new ResponseEntity<>(result, HttpHeaders.EMPTY, HttpStatus.OK);
//        String result = new String(waitingService.getQueuePage(targetUrl).getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        return ResponseEntity.ok().headers(headers).body(result);
    }

    @PostMapping
    public ResponseEntity<?> enqueue(HttpServletRequest request) {
        String targetUrl = request.getHeader("Target-URL");
//        log.info("-------------------------- enqueue api called. in controller -------------------------------");
//        log.info("targetUrl = {}", targetUrl);
        EnqueueResponse result = waitingService.enqueue(targetUrl);
        return ResponseEntity
                .ok(new SuccessResponse(HttpStatus.OK.value(), "대기열에 줄을 섰습니다.", result));
    }


    @PostMapping("/order")
    public ResponseEntity<WaitingOrderResponse> getMyOrder(@RequestBody WaitingOrderRequest request) {
        WaitingOrderResponse myOrderRes = waitingService.getMyOrder(request.queueId(), request.waiterId());
        return ResponseEntity
                .ok(myOrderRes);
    }

    @GetMapping("/page-req")
    public ResponseEntity<?> forwardToTarget(@RequestParam(value = "token") String token) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("text", "html", StandardCharsets.UTF_8));
//        headers.setContentType(MediaType.TEXT_HTML);
//        headers.set("Content-Encoding", "UTF-8");

        String result = waitingService.forward(token);
//        log.info("target page 포워딩 api called");

        return ResponseEntity.ok().headers(headers).body(result);

//        return new ResponseEntity<>(result, headers, HttpStatus.OK);
//        return ResponseEntity
//                .ok(waitingService.forward(token, request));
    }

    // 이탈. 대기 페이지가 창을 닫을 때 sendBeacon으로도 보낼 수 있게 POST와 요청 파라미터(쿼리 문자열이나 form 본문)로 받는다.
    @PostMapping("/out")
    public ResponseEntity<Void> out(@RequestParam(value = "queueId") String queueId,
                                    @RequestParam(value = "waiterId") String waiterId) {
        waitingService.out(queueId, waiterId);
        return ResponseEntity
                .ok()
                .build();
    }

    @PostMapping("/{queueId}/activate")
    public ResponseEntity<?> activateQueue(@PathVariable("queueId") String queueId) {
        waitingService.activate(queueId);
        return ResponseEntity
                .ok(new SuccessResponse(HttpStatus.OK.value(), "활성화되었습니다."));
    }

    @PostMapping("/{queueId}/deactivate")
    public ResponseEntity<?> deactivateQueue(@PathVariable("queueId") String queueId) {
        waitingService.deactivate(queueId);
        return ResponseEntity
                .ok(new SuccessResponse(HttpStatus.OK.value(), "비활성화되었습니다."));
    }

    @GetMapping
    public String forwardingTest() {
       return "forwarding success!";
    }

    @GetMapping(path = "/next")
    public ResponseEntity<?> parsingFile(@RequestHeader("address") String address,
                                         @RequestHeader("scheme") String scheme) {

        ResponseEntity<?> result = waitingService.parsing(address, scheme);

        return result;

    }
}
