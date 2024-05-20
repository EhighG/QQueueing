//package com.qqueueing.consumer.job.config;
//
//import com.qqueueing.consumer.job.tasklet.QueueingTasklet;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.job.builder.JobBuilder;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.batch.core.step.builder.StepBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.transaction.PlatformTransactionManager;
//
//@Slf4j
//@Configuration
//@RequiredArgsConstructor
//public class QueueingJobConfig {
//
//    private final QueueingTasklet queingTasklet;
//    private final JobRepository jobRepository;
//    private final PlatformTransactionManager platformTransactionManager;
//
//    @Bean
//    public Job QueingJob() {
//        return new JobBuilder("queingJob", jobRepository)
//                .start(queingStep())
//                .build();
//    }
//
//
//    @Bean
//    public Step queingStep(){
//        return new StepBuilder("queingStep", jobRepository)
//                .tasklet(queingTasklet, platformTransactionManager)
//                .build();
//    }
//
//
//}
