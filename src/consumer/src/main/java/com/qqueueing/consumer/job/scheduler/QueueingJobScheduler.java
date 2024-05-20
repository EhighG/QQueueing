package com.qqueueing.consumer.job.scheduler;//package com.test.batchqueing.job.scheduler;
//
//import com.test.batchqueing.job.config.QueingJobConfig;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.batch.core.*;
//import org.springframework.batch.core.launch.JobLauncher;
//import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
//import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
//import org.springframework.batch.core.repository.JobRestartException;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.util.Date;
//
//@RequiredArgsConstructor
//@Component
//@Configuration
//@Slf4j
//public class QueingJobScheduler {
//
//    private final JobLauncher jobLauncher;
//    private final QueingJobConfig queingJobConfig;
//
//    @Scheduled(cron = "*/10 * * * * *")
//    public void jobSchduled() throws JobParametersInvalidException, JobExecutionAlreadyRunningException,
//            JobRestartException, JobInstanceAlreadyCompleteException {
//
//        Job queingJob = queingJobConfig.QueingJob();
//
//        log.info("queing job schedule 실행");
//
//        JobParameters parameters = new JobParametersBuilder().addDate("date", new Date()).toJobParameters();
//
//        JobExecution jobExecution = jobLauncher.run(queingJob, parameters);
//
//        while (jobExecution.isRunning()) {
//            log.info("...");
//        }
//
//        log.info("Job Execution: " + jobExecution.getStatus());
////        log.info("Job getJobConfigurationName: " + jobExecution.getJobConfigurationName());
//        log.info("Job getJobId: " + jobExecution.getJobId());
//        log.info("Job getExitStatus: " + jobExecution.getExitStatus());
//        log.info("Job getJobInstance: " + jobExecution.getJobInstance());
//        log.info("Job getStepExecutions: " + jobExecution.getStepExecutions());
//        log.info("Job getLastUpdated: " + jobExecution.getLastUpdated());
//        log.info("Job getFailureExceptions: " + jobExecution.getFailureExceptions());
//
//    }
//
//}
