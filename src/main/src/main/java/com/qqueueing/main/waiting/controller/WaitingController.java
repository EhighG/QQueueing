package com.qqueueing.main.waiting.controller;


import com.qqueueing.main.common.SuccessResponse;
import com.qqueueing.main.waiting.model.GetMyOrderReqDto;
import com.qqueueing.main.waiting.model.GetMyOrderResDto;
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

    @GetMapping("/write")
    public Object produceTest(@RequestParam(value = "partitionNo") Integer partitionNo,
                              @RequestParam(value = "key") Long key,
                              @RequestParam(value = "message") String message) {
        return waitingService.send(partitionNo, key, message);
    }

    @GetMapping("/enter")
    public ResponseEntity<?> enter(HttpServletRequest request) {
        String targetUrl = request.getHeader("Target-URL");
        if (targetUrl == null) targetUrl = request.getRequestURL().toString();

        URI redirectUrl = waitingService.enter(targetUrl);
        log.info("redirectUrl.toString() = {}", redirectUrl.toString());
        return ResponseEntity
                .status(302)
                .location(redirectUrl) // queue-page or target-page
                .build();
    }

    @GetMapping("/queue-page")
    public ResponseEntity<?> getQueuePage(@RequestParam(value = "Target-URL") String targetUrl) {
        log.info("targetUrl = {}", targetUrl);
        log.info("queue-page 포워딩 api called");

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
        log.info("-------------------------- enqueue api called. in controller -------------------------------");
        log.info("targetUrl = {}", targetUrl);
        Object result = waitingService.enqueue(targetUrl, request);
        return ResponseEntity
                .ok(new SuccessResponse(HttpStatus.OK.value(), "대기열에 입장되었습니다.", result));
    }


    @PostMapping("/order")
    public ResponseEntity<?> getMyOrder(@RequestBody GetMyOrderReqDto getMyOrderReqDto) {
        GetMyOrderResDto myOrderRes = waitingService.getMyOrder(getMyOrderReqDto.getPartitionNo(),
                getMyOrderReqDto.getOrder(),
                getMyOrderReqDto.getIdVal());
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
        log.info("target page 포워딩 api called");

        return ResponseEntity.ok().headers(headers).body(result);

//        return new ResponseEntity<>(result, headers, HttpStatus.OK);
//        return ResponseEntity
//                .ok(waitingService.forward(token, request));
    }

    @GetMapping("/out")
    public ResponseEntity<Void> out(@RequestParam(value = "partitionNo") int partitionNo,
                                    @RequestParam(value = "order") Long order) {
        log.info("out() called");
        waitingService.out(partitionNo, order);
        log.info("out end. order = {} // end", order);
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

    @GetMapping(path = "/next", headers = "address")
    public ResponseEntity<?> parsingFile(@RequestHeader("address") String address) {

        ResponseEntity<?> result = waitingService.parsing(address);

        return result;

    }

//    @GetMapping("/testQueuePage")
//    public String testQueuePage() {
//        return "<!DOCTYPE html>\n" +
//                "<html lang=\"en\">\n" +
//                "<head>\n" +
//                "    <meta charset=\"UTF-8\">\n" +
//                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
//                "    <title>TargetUrl Response Page</title>\n" +
//                "</head>\n" +
//                "<body>\n" +
//                "    \n" +
//                "</body>\n" +
//                "</html>";
//    }

    // 여러 대기열 테스트용 메소드들

//    @PostMapping("/testlink2")
//    public ResponseEntity<?> enter2(HttpServletRequest request, HttpServletResponse response) {
//        Object result = waitingService.enter(request);
//        if (result != null) {
//            return ResponseEntity
//                    .ok(new SuccessResponse(HttpStatus.OK.value(), "대기열에 입장되었습니다.", result));
//        }
//        try {
//            // 대기열 비활성화 시 -> redirect
//            return ResponseEntity
//                    .status(302)
//                    .location(new URI(frontUrl))
//                    .build();
//        } catch (URISyntaxException e) {
//            log.error(e.getMessage());
//            throw new RuntimeException("리다이렉트 설정 중 에러");
//        }
//    }
}
