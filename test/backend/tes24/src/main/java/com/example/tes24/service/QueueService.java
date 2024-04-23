package com.example.tes24.service;

import org.springframework.http.ResponseEntity;

public interface QueueService {
    ResponseEntity<?> enqueue(Long memberId);
    ResponseEntity<?> dequeue();
}
