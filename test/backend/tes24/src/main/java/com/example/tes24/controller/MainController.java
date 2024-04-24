package com.example.tes24.controller;

import com.example.tes24.dto.WaitingStatusResponseRecord;
import com.example.tes24.service.MainService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/tes24")
@RequiredArgsConstructor
@Tag(name = "Main API")
public class MainController {
    private final MainService mainService;
    @GetMapping
    public ResponseEntity<?> home() {
        return ResponseEntity.ok("This is home page.");
    }

    @Operation(
            summary = "Request waiting status",
            description = "Calling this api will response with current waiting status include current queue size and order.",
            responses =
                    @ApiResponse(
                            responseCode = "200",
                            description = "Request successes.",
                            content = @Content(schema = @Schema(name = "response", implementation = WaitingStatusResponseRecord.class))))
    @PostMapping("/waiting/{waitingIdx}")
    public ResponseEntity<WaitingStatusResponseRecord> waiting(@PathVariable Long waitingIdx, @RequestBody String idVal) {
        ResponseEntity<WaitingStatusResponseRecord> result = mainService.getWaitingStatus(waitingIdx, idVal);

        WaitingStatusResponseRecord waitingStatusResponseRecord = result.getBody();
        if (waitingStatusResponseRecord.totalQueueSize() == -1) {
            log.info(waitingStatusResponseRecord.redirectUrl());
            return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).header("Location", "http://" + waitingStatusResponseRecord.redirectUrl()).build();
        }

        return mainService.getWaitingStatus(waitingIdx, idVal);
    }
}
