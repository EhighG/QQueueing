package com.qqueueing.main.config;

import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME)
    public Executor asyncTaskExecutor() {
        TaskExecutorAdapter taskExecutorAdapter = new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
        taskExecutorAdapter.setTaskDecorator(new MDCTaskDecorator());
        return taskExecutorAdapter;
    }
}