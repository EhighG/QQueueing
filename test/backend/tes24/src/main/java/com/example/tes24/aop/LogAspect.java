package com.example.tes24.aop;

import com.example.tes24.entity.DequeueLog;
import com.example.tes24.entity.EnqueueLog;
import com.example.tes24.repository.DequeueLogRepository;
import com.example.tes24.repository.EnqueueLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;

@Slf4j
@Aspect
//@Component
@RequiredArgsConstructor
public class LogAspect {
    private final EnqueueLogRepository enqueueLogRepository;
    private final DequeueLogRepository dequeueLogRepository;

    @Pointcut("execution(* com.example.tes24.controller.MemberController.enqueue(..))")
    private void enqueue() {}

    @Pointcut("execution(* com.example.tes24.controller.MemberController.dequeue(..))")
    private void dequeue() {}

    @Around("enqueue()")
    public Object logEnqueue(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Long memberId = null;
        Long sequence = null;

        Object[] args = proceedingJoinPoint.getArgs();
        for (Object arg : args) {
            switch (arg.getClass().getName()) {
                case "memberId" -> memberId = (Long) arg;
                case "sequence" -> sequence = (Long) arg;
                default -> {}
            }
        }

        EnqueueLog enqueueLog = new EnqueueLog();
        enqueueLog.setMemberId(memberId);
        enqueueLog.setEnqueueTime(LocalDateTime.now());
        if (sequence != null) enqueueLog.setSequenceNumber(sequence);

        enqueueLogRepository.save(enqueueLog);

        log.info("logging enqueueing time: " + (System.currentTimeMillis() - start) + " ms");

        return proceedingJoinPoint.proceed();
    }

    @Around("dequeue()")
    public Object logDequeue(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Long memberId = null;
        Long sequence = null;

        Object[] args = proceedingJoinPoint.getArgs();
        for (Object arg : args) {
            switch (arg.getClass().getName()) {
                case "memberId" -> memberId = (Long) arg;
                case "sequence" -> sequence = (Long) arg;
                default -> {}
            }
        }

        DequeueLog dequeueLog = new DequeueLog();
        dequeueLog.setMemberId(memberId);
        dequeueLog.setDequeueTime(LocalDateTime.from(Instant.now()));
        if (sequence != null) dequeueLog.setSequenceNumber(sequence);

        dequeueLogRepository.save(dequeueLog);

        log.info("logging dequeueing time: " + (System.currentTimeMillis() - start) + " ms");

        return proceedingJoinPoint.proceed();
    }

}
