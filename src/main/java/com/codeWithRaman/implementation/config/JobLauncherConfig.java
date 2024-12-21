package com.codeWithRaman.implementation.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobLauncherConfig {

	private static final Logger logger = LoggerFactory.getLogger(JobLauncherConfig.class);
	
	@Bean
	public CommandLineRunner runJob(JobLauncher jobLauncher, Job job) {
		return args -> {
			try {
				logger.info("Starting the Job ......");
				JobParameters jobParameter = new JobParametersBuilder().addLong("time", System.currentTimeMillis()).toJobParameters();
				JobExecution execution = jobLauncher.run(job, jobParameter);

                logger.info("Job Status: {}", execution.getStatus());
                logger.info("Job completed successfully.");
            } catch (Exception e) {
                logger.error("Job failed to execute.", e);
            }
		};
	}
}
