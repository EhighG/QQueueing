package com.qqueueing.consumer.job.tasklet;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class QueueingTasklet implements Tasklet, StepExecutionListener {


    @Override
    public void beforeStep(StepExecution stepExecution) {

        log.info("before queing");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {

        log.info("after queing");

        return ExitStatus.COMPLETED;
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {



        return RepeatStatus.FINISHED;
    }
}
