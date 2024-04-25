package com.example.tes24.service;

import com.example.tes24.dto.EnqueueResponse;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.Future;

public interface QueueService {
    Future<EnqueueResponse> enqueue();
    ResponseEntity<?> dequeue();
}
