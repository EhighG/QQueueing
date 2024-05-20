package com.qqueueing.main.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ForkJoinPool;

@Configuration
public class VirtualConfig {

    @Bean
    public ForkJoinPool forkJoinPool() {
        return new ForkJoinPool();
    }
}
