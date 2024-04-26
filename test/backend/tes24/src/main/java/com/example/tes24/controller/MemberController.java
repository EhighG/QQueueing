package com.example.tes24.controller;

import com.example.tes24.entity.Member;
import com.example.tes24.security.userdetails.JwtUserDetails;
import com.example.tes24.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
@Tag(name = "Member API")
public class MemberController {
    private final MemberService memberService;

    @Operation(
            summary = "Sign up a new member",
            description = "Saving a member to the database.",
            responses ={
                    @ApiResponse(
                            responseCode = "200",
                            description = "Sign up successes.",
                            content = @Content(schema = @Schema(name = "response", implementation = Member.class))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Sign up failed.",
                            content = @Content(schema = @Schema(name = "Can't sign up.", implementation = String.class)))
            })

    @PostMapping("/signup")
    public ResponseEntity<?> signUp() {
        var response = memberService.signUp(new Member());
        if (response != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.internalServerError().body("Can't sign up.");
        }
    }

    @Operation(
            summary = "Log in a member",
            description = "Generating a access token and a refresh token. After that, sending token to the client.",
            responses =
            @ApiResponse(
                    responseCode = "200",
                    description = "Log in successes.",
                    content = @Content(schema = @Schema(name = "response", implementation = Member.class))))
    @PostMapping("/login")
    public ResponseEntity<?> login(@AuthenticationPrincipal(expression = JwtUserDetails.ID) Long memberId) {
        Member member = memberService.login(memberId);
        return null;
    }

    @PostMapping("/enqueue")
    public ResponseEntity<?> enqueue() {
        return ResponseEntity.ok("enqueued");
    }

    @PostMapping("/dequeue")
    public ResponseEntity<?> dequeue() {
        return ResponseEntity.ok("dequeued");
    }
}
