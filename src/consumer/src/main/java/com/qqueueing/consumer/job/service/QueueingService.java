package com.qqueueing.consumer.job.service;

import com.qqueueing.consumer.job.config.QueueingJobConfig;
import com.qqueueing.consumer.job.dto.StartBatchReqDto;
import com.qqueueing.consumer.job.exception.QueueingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Service
public class QueueingService {

    private final JobLauncher jobLauncher;
    private final QueueingJobConfig queingJobConfig;

    public void startBatch(StartBatchReqDto startBatchReqDto) {

        try {

            System.out.println("들어온 데이터 : " + startBatchReqDto.getNum());

            Job queingJob = queingJobConfig.QueingJob();

            log.info("queing job schedule 실행");

            JobParameters parameters = new JobParametersBuilder().addDate("date", new Date()).toJobParameters();

            JobExecution jobExecution = jobLauncher.run(queingJob, parameters);

            while (jobExecution.isRunning()) {
                log.info("...");
            }

            log.info("Job Execution: " + jobExecution.getStatus());
            log.info("Job getJobId: " + jobExecution.getJobId());
            log.info("Job getExitStatus: " + jobExecution.getExitStatus());
            log.info("Job getJobInstance: " + jobExecution.getJobInstance());
            log.info("Job getStepExecutions: " + jobExecution.getStepExecutions());
            log.info("Job getLastUpdated: " + jobExecution.getLastUpdated());
            log.info("Job getFailureExceptions: " + jobExecution.getFailureExceptions());

        } catch (Exception e) {

            throw new QueueingException();
        }
    }
}
