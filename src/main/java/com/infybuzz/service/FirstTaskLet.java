package com.infybuzz.service;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Service;

@Service
public class FirstTaskLet implements Tasklet {
    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        System.out.println("********* 1 ********** This is First tasklet step");
        System.out.println("SGOSHIKA: Printing JEC from inside the step: "+chunkContext.getStepContext().getJobExecutionContext());
        System.out.println("SGOSHIK: Printing SEC from inside the step: "+chunkContext.getStepContext().getStepExecutionContext());
        return RepeatStatus.FINISHED;
    }
}
