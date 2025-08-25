package com.infybuzz.config;

import com.infybuzz.model.StudentCSV;
import com.infybuzz.model.StudentJDBC;
import com.infybuzz.model.StudentJSON;
import com.infybuzz.model.StudentResponse;
import com.infybuzz.processor.FirstItemProcessor;
import com.infybuzz.reader.FirstItemReader;
import com.infybuzz.service.FirstTaskLet;
import com.infybuzz.service.SecondTaskLet;
import com.infybuzz.service.StudentService;
import com.infybuzz.writer.FirstItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.adapter.ItemReaderAdapter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;
import java.io.File;

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
	private FirstItemReader firstItemReader;
	@Autowired
	private FirstItemProcessor firstItemProcessor;
	@Autowired
	private FirstItemWriter firstItemWriter;

	@Autowired
	private DataSource dataSource;

	@Autowired
	private StudentService studentService;

//	@Bean
	public Job firstJob() {
		return jobBuilderFactory.get("First Job")
				.incrementer(new RunIdIncrementer())
				.start(firstStep())
				.next(secondStep())
				.build();
	}

	private Step firstStep() {
		return stepBuilderFactory.get("Tasklet Job - First Step")
//				.allowStartIfComplete(true)
				.tasklet(firstTaskLet)
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
		return stepBuilderFactory.get("Tasklet Job - Second Step")
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
		return stepBuilderFactory.get("******* Chunk Oriented Step")
				.<StudentResponse,StudentResponse>chunk(3)
//				.reader(flatFileItemReader(null))
//				.reader(jsonItemReader(null))
				.reader(restAPIReader())
//				.processor(firstItemProcessor)
				.writer(firstItemWriter)
				.build();
	}

	@StepScope
	@Bean
	public FlatFileItemReader<StudentCSV> flatFileItemReader(
			@Value("#{jobParameters['inputFile']}") FileSystemResource fileSystemResource){
		FlatFileItemReader<StudentCSV> flatFileItemReader=new FlatFileItemReader<>();
		flatFileItemReader.setResource(fileSystemResource);
		flatFileItemReader.setLineMapper(new DefaultLineMapper<StudentCSV>(){
			{
				setLineTokenizer(new DelimitedLineTokenizer(){
					{
						setNames("ID","First Name","Last Name","Email");
						setDelimiter("|");
					}
				});

				setFieldSetMapper(new BeanWrapperFieldSetMapper<StudentCSV>(){
					{setTargetType(StudentCSV.class);}
				});
			}
		});

		flatFileItemReader.setLinesToSkip(1);

		return flatFileItemReader;
	}

	@StepScope
	@Bean
	public JsonItemReader<StudentJSON> jsonItemReader(
			@Value("#{jobParameters['inputFile']}") FileSystemResource fileSystemResource)
	{
		JsonItemReader<StudentJSON> jsonItemReader=new JsonItemReader<StudentJSON>();
		jsonItemReader.setResource(fileSystemResource);

		JacksonJsonObjectReader jacksonJsonObjectReader=new JacksonJsonObjectReader<>(StudentJSON.class);

		jsonItemReader.setJsonObjectReader(jacksonJsonObjectReader);
		jsonItemReader.setMaxItemCount(3);
		jsonItemReader.setCurrentItemCount(2);

		return jsonItemReader;
	}

	@Bean
	@Primary
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource springDatasource(){
		return DataSourceBuilder.create().build();
	}

	@Bean
	@ConfigurationProperties(prefix = "spring.universitydatasource")
	public DataSource universityDatasource(){
		return DataSourceBuilder.create().build();
	}

	@StepScope
	@Bean
	public JdbcCursorItemReader<StudentJDBC> jdbcCursorItemReader(){
		JdbcCursorItemReader jdbcCursorItemReader=new JdbcCursorItemReader();

		jdbcCursorItemReader.setDataSource(universityDatasource());
		jdbcCursorItemReader.setSql("select id, first_name, last_name, email from student");
		jdbcCursorItemReader.setRowMapper(new BeanPropertyRowMapper(){
			{
				setMappedClass(StudentJDBC.class);
			}
		});
		return jdbcCursorItemReader;
	}

	@Bean
	@StepScope
	public ItemReaderAdapter<StudentResponse> restAPIReader(){
		ItemReaderAdapter<StudentResponse> itemReaderAdapter=new ItemReaderAdapter<>();

		itemReaderAdapter.setTargetObject(studentService);
		itemReaderAdapter.setTargetMethod("getStudent");

		return itemReaderAdapter;
	}

}
