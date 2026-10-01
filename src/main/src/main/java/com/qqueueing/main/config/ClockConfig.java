package com.qqueueing.main.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * 입장 처리가 쓰는 시계. 테스트는 @Primary 시계 빈으로 바꿔 끼운다.
 */
@Configuration
public class ClockConfig {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
