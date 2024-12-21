package com.codeWithRaman.implementation.config;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class SpringBatchConfig {
	
	private final JobRepository jobRepository;
	private final PlatformTransactionManager platformTransactionManager;
	private final DataSource dataSource;
	public SpringBatchConfig(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager,
			DataSource dataSource) {
		this.jobRepository = jobRepository;
		this.platformTransactionManager = platformTransactionManager;
		this.dataSource = dataSource;
	}
	
	@Bean
	public JdbcCursorItemReader<String> reader(){
		return new JdbcCursorItemReaderBuilder<String>().dataSource(dataSource).sql("SELECT name FROM input_names").rowMapper((rs,rowNum) -> rs.getString("name"))
		.name("jdbcReader").build();
	}
	
	@Bean
	public ItemProcessor<String, String> process(){
		return name -> "Hello "+name+ "!";
	}
	
	@Bean
    public JdbcBatchItemWriter<String> writer() {
        return new JdbcBatchItemWriterBuilder<String>()
                .dataSource(dataSource)
                .sql("INSERT INTO output_greetings (greeting) VALUES (:greeting)")
                .itemSqlParameterSourceProvider(greeting -> {
                    var paramSource = new org.springframework.jdbc.core.namedparam.MapSqlParameterSource();
                    paramSource.addValue("greeting", greeting);
                    return paramSource;
                })
                .build();
    }
	
	@Bean
	public Step step() {
		return new StepBuilder("step", jobRepository).<String,String>chunk(10, platformTransactionManager)
				.reader(reader())
				.processor(process())
				.writer(writer())
				.build();
	}
	
	@Bean
	public Job job() {
		return new JobBuilder("job", jobRepository).start(step()).build();
	}
}
