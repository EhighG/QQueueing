package com.qqueueing.main.log.controller;

import com.qqueueing.main.common.SuccessResponse;
import com.qqueueing.main.log.dto.SearchLogsResDto;
import com.qqueueing.main.log.service.LogApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class LogApiController {

    private final LogApiService logApiService;

    @GetMapping("/logs")
    public ResponseEntity<?> searchLogs() {

        SearchLogsResDto result = logApiService.searchLogs();

        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK.value(), "사용자 서버의 로그들을 조회하였습니다.", result));
    }
}
