package com.infybuzz.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class FirstJobListener implements JobExecutionListener {
    @Override
    public void beforeJob(JobExecution jobExecution) {
        System.out.println("****************************** Job Listener: Befor Job: ******************************");
        System.out.println(jobExecution.getJobParameters());
        System.out.println(jobExecution.getExecutionContext());

        jobExecution.getExecutionContext().put("JEC","JEC Value");

        System.out.println("****************************** ");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        System.out.println("****************************** Job Listener: After Job: ****************************** ");
        System.out.println(jobExecution.getJobParameters());
        System.out.println(jobExecution.getExecutionContext());
        System.out.println("****************************** ");
    }
}
