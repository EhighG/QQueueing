package com.example.tes24.controller;

import com.example.tes24.dto.WaitingRequest;
import com.example.tes24.dto.WaitingStatusResponse;
import com.example.tes24.qqueueing.dto.Q2ClientRequest;
import com.example.tes24.qqueueing.dto.Q2ServerResponse;
import com.example.tes24.service.MainService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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

    @Operation(
            summary = "For redirecting page",
            responses =
            @ApiResponse(
                    responseCode = "200",
                    description = "Redirect success.",
                    content =
                    @Content(
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "This is home page."))))
    @GetMapping
    public ResponseEntity<?> home() {
        return ResponseEntity.ok("This is home page.");
    }

    @Operation(
            summary = "Request waiting status",
            description = "Calling this api will response with current waiting status include current queue size and order.",
            parameters = @Parameter(name = "waitingIdx", schema = @Schema(implementation = Long.class)),
            requestBody = @io.swagger.v3.oas.annotations.parameters.
                    RequestBody(content = @Content(schema = @Schema(name = "waitingRequest", implementation = WaitingRequest.class)), required = true),
            responses =
            @ApiResponse(
                    responseCode = "200",
                    description = "Request success.",
                    content = @Content(schema = @Schema(name = "response", implementation = WaitingStatusResponse.class))))
    @PostMapping("/waiting/{waitingIdx}")
    public ResponseEntity<WaitingStatusResponse> waiting(@PathVariable Long waitingIdx, @RequestBody WaitingRequest waitingRequest) {
        WaitingStatusResponse response = mainService.getWaitingStatus(waitingIdx, waitingRequest.idVal());

        if (response.hasCurrentTurn()) {
            log.info(response.myOrder() + " " + response.redirectUrl());
            return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).header("Location", "http://" + response.redirectUrl()).build();
        }

        return ResponseEntity.ok().body(response);
    }

    @PostMapping
    public ResponseEntity<?> test(@RequestBody Q2ClientRequest q2ClientRequest) {
        Q2ServerResponse q2ServerResponse = new Q2ServerResponse();
        q2ServerResponse.setClientId(q2ClientRequest.getClientId());
        q2ServerResponse.setClientKey(q2ClientRequest.getClientKey());
        q2ServerResponse.setUserId(q2ClientRequest.getUserId());
        q2ServerResponse.setUserKey(q2ClientRequest.getUserKey());
        q2ServerResponse.setCapacity(1000L);
        q2ServerResponse.setUserSequence(10L);
        q2ServerResponse.setCurrentQueueSize(Long.MAX_VALUE);
        return ResponseEntity.ok(q2ServerResponse);
    }
}
