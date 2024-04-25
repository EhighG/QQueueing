package com.example.tes24.controller;

import com.example.tes24.dto.EnqueueResponse;
import com.example.tes24.entity.Member;
import com.example.tes24.security.userdetails.JwtUserDetails;
import com.example.tes24.service.MemberService;
import com.example.tes24.service.QueueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
@Tag(name = "Member API")
public class MemberController {
    private final MemberService memberService;
    private final QueueService queueService;

    @Operation(
            summary = "Sign up a new member",
            description = "Saving a member to the database.",
            responses =
            @ApiResponse(
                    responseCode = "200",
                    description = "Sign up success.",
                    content = @Content(schema = @Schema(name = "response", implementation = Member.class))))
    @PostMapping("/signup")
    public ResponseEntity<Member> signUp() {
        Member response = memberService.signUp(new Member());
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Log in a member",
            description = "Generating a access token and a refresh token. After that, sending token to the client.",
            responses =
            @ApiResponse(
                    responseCode = "200",
                    description = "Log in success.",
                    content = @Content(schema = @Schema(name = "response", implementation = Member.class))))
    @PostMapping("/login")
    public ResponseEntity<Member> login(@AuthenticationPrincipal(expression = JwtUserDetails.ID) Long memberId) {
        Member member = memberService.login(memberId);

        return ResponseEntity.ok(member);
    }

    @Operation(
            summary = "Enqueue a user",
            description = "Request for entering waiting room to api server.",
            responses =
            @ApiResponse(
                    responseCode = "200",
                    description = "Enqueueing success. Respond with a order number which is used to identify the user.",
                    content = @Content(schema = @Schema(name = "response", implementation = EnqueueResponse.class))))
    @PostMapping("/enqueue")
    public ResponseEntity<EnqueueResponse> enqueue() throws ExecutionException, InterruptedException {
        Future<EnqueueResponse> future = queueService.enqueue();
        EnqueueResponse response = future.get();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Dequeue a user",
            description = "Request for exiting waiting room to api server. Not yet implemented.",
            responses =
            @ApiResponse(
                    responseCode = "200",
                    description = "Dequeueing success."))
    @PostMapping("/dequeue")
    public ResponseEntity<?> dequeue() {
        return queueService.dequeue();
    }
}
