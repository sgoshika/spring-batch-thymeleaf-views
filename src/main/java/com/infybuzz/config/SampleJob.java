package com.infybuzz.config;

import com.infybuzz.listener.FirstJobListener;
import com.infybuzz.listener.FirstStepListener;
import com.infybuzz.processor.FirstItemProcessor;
import com.infybuzz.reader.FirstItemReader;
import com.infybuzz.service.FirstTaskLet;
import com.infybuzz.service.SecondTaskLet;
import com.infybuzz.writer.FirstItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SampleJob {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	@Autowired
	private FirstTaskLet firstTaskLet;
	@Autowired
	private SecondTaskLet secondTaskLet;
	@Autowired
	private FirstJobListener firstJobListener;
	@Autowired
	private FirstStepListener firstStepListener;

	@Autowired
	private FirstItemReader firstItemReader;
	@Autowired
	private FirstItemProcessor firstItemProcessor;
	@Autowired
	private FirstItemWriter firstItemWriter;


	@Bean
	public Job firstJob() {
		return jobBuilderFactory.get("First Job")
				.incrementer(new RunIdIncrementer())
				.start(firstStep())
				.next(secondStep())
				.listener(firstJobListener)
				.build();
	}

	private Step firstStep() {
		return stepBuilderFactory.get("First Step")
//				.allowStartIfComplete(true)
				.tasklet(firstTaskLet)
				.listener(firstStepListener)
				.build();
	}

//	private Tasklet firstTask() {
//		return new Tasklet() {
//
//			@Override
//			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
//				System.out.println("This is first tasklet step");
//				return RepeatStatus.FINISHED;
//			}
//		};
//	}

	private Step secondStep() {
		return stepBuilderFactory.get("Second Step")
//				.allowStartIfComplete(true)
				.tasklet(secondTaskLet)
				.build();
	}

//	*************************** Chunk Oriented Job ***********************************
	@Bean
	public Job secondJob(){
		return jobBuilderFactory.get("Second Job")
				.incrementer(new RunIdIncrementer())
				.start(firstChunkStep())
				.build();
	}

	private Step firstChunkStep() {
		return stepBuilderFactory.get("First Chunk Oriented Step")
				.<Integer,Long>chunk(3)
				.reader(firstItemReader)
				.processor(firstItemProcessor)
				.writer(firstItemWriter)
				.build();
	}
}
