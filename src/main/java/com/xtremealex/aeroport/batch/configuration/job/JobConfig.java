package com.xtremealex.aeroport.batch.configuration.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.BatchConfigurationException;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import java.util.HashMap;
import java.util.Map;

@EnableBatchProcessing
@Configuration
public class JobConfig {

    private final JobRepository jobRepository;

    public JobConfig(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    //@Bean
    public Map<String, Job> jobs(
            @Qualifier("importAirportTypeJob") Job importAirportTypeJob,
            @Qualifier("importAirportJob") Job importAirportJob) {
        Map<String, Job> jobs = new HashMap<>();
        jobs.put("importAirportTypeJob", importAirportTypeJob);
        jobs.put("importAirportJob", importAirportJob);
        return jobs;
    }

    @Bean
    @Qualifier("processJob")
    public Job processJob(
            @Qualifier("countObjJson") Step step0,
            @Qualifier("step1") Step step1,
            @Qualifier("step2") Step step2) {
        return new JobBuilder("processJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step0)
                .next(step1)
                .next(step2)
                .build();
    }

    @Bean
    @Qualifier("countObjJsonJob")
    public Job countObjJsonJob(@Qualifier("countObjJson") Step step0) {
        return new JobBuilder("countObjJsonJob", jobRepository)
                .start(step0)
                .incrementer(new RunIdIncrementer())
                .build();
    }


    @Bean
    @Qualifier("importAirportTypeJob")
    public Job importAirportTypeJob(
            @Qualifier("countObjJson") Step step0,
            @Qualifier("step1") Step step1) {
        return new JobBuilder("importAirportTypeJob", jobRepository)
                .start(step0)
                .next(step1)
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    @Qualifier("importAirportJob")
    public Job importAirportJob(
            @Qualifier("step2")Step step2,
            @Qualifier("countObjJson") Step step0) {
        return new JobBuilder("importAirportJob", jobRepository)
                .start(step0)
                .next(step2)
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    @Qualifier("jobLauncherOld")
    public JobLauncher jobLauncherOld(final JobRepository jobRepository) {
        //FIXME a che serve provare ad usare un metodo annotato @Deprecated???
        SimpleJobLauncher simpleJobLauncher = new SimpleJobLauncher();
        simpleJobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
        simpleJobLauncher.setJobRepository(jobRepository);
        return simpleJobLauncher;
    }

    @Bean
    public JobLauncher jobLauncher(final JobRepository jobRepository) throws BatchConfigurationException {
        TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
        //jobLauncher.setTaskExecutor(new SyncTaskExecutor());
        try {
            jobLauncher.afterPropertiesSet();
            return jobLauncher;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
