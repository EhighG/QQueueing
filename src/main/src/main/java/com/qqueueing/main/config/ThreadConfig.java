//package com.qqueueing.main.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//
//import java.util.concurrent.Executor;
//
////@EnableAsync
//@Configuration
//public class ThreadConfig {
//
//    @Bean
//    public Executor taskExecutor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(120);
//        executor.setMaxPoolSize(200);
//        executor.setQueueCapacity(500);
//        executor.initialize();
//        return executor;
//    }
//}
