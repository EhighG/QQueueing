package com.example.tes24.aop;

import com.example.tes24.repository.DequeueLogRepository;
import com.example.tes24.repository.EnqueueLogRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;

@Aspect
@RequiredArgsConstructor
public class LogAspect {
    private final EnqueueLogRepository enqueueLogRepository;
    private final DequeueLogRepository dequeueLogRepository;


}
