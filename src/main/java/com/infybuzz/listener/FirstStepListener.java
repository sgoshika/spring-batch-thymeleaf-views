package com.infybuzz.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class FirstStepListener implements StepExecutionListener {
    @Override
    public void beforeStep(StepExecution stepExecution) {
        System.out.println("*************Step Listener: BEFORE: ");

        System.out.println(stepExecution.getStepName());
        System.out.println(stepExecution.getJobExecution().getExecutionContext());
        System.out.println(stepExecution.getExecutionContext());

        stepExecution.getExecutionContext().put("SEC","SEC Value");

        System.out.println("************************************************");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        System.out.println("*************Step Listener: AFTER: ");
        System.out.println(stepExecution.getStepName());
        System.out.println(stepExecution.getJobExecution().getExecutionContext());
        System.out.println(stepExecution.getExecutionContext());

        System.out.println("************************************************");
        return null;
    }
}
