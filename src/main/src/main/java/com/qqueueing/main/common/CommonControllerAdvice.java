//package com.qqueueing.main.common;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//@Slf4j
//@RestControllerAdvice
//public class CommonControllerAdvice {
//
//    @ExceptionHandler(RuntimeException.class)
//    public ResponseEntity<?> handleRuntimeException(RuntimeException e) {
//        log.error("에러 발생");
//        e.printStackTrace();
//        return ResponseEntity
//                .ok(new FailResponse(200, e.getMessage()));
//    }
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<?> handleAllException(Exception e) {
//        log.error("에러 발생");
//        e.printStackTrace();
//        return ResponseEntity
//                .ok(new FailResponse(500, e.getMessage()));
//    }
//}
