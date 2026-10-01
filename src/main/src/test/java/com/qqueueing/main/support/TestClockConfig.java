package com.qqueueing.main.support;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.Instant;
import java.time.ZoneOffset;

/**
 * 운영 시계(ClockConfig.clock) 대신 테스트가 정하는 시계를 주입한다.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestClockConfig {

    public static final Instant START = Instant.parse("2026-01-01T00:00:00Z");

    @Bean
    @Primary
    MutableClock testClock() {
        return new MutableClock(START, ZoneOffset.UTC);
    }
}
