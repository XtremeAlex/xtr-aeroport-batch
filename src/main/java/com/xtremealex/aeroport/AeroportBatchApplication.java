package com.xtremealex.aeroport;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Date;

@SpringBootApplication
@EnableJpaAuditing
@ComponentScan(basePackages = {"com.xtremealex.aeroport.batch.configuration", "com.xtremealex.aeroport"})
public class AeroportBatchApplication implements CommandLineRunner {

    @Autowired
    private JobLauncher jobLauncher;

    @Qualifier("processJob")
    @Autowired
    private Job processJob;


    @Qualifier("countObjJsonJob")
    @Autowired
    private Job countObjJsonJob;

    @Qualifier("importAirportTypeJob")
    @Autowired
    private Job importAirportTypeJob;

    @Qualifier("importAirportJob")
    @Autowired
    private Job importAirportJob;

    public static void main(String[] args) {
        SpringApplication.run(AeroportBatchApplication.class, args);
    }


    public void startTest(String... args) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        ConfigurableApplicationContext context = SpringApplication.run(AeroportBatchApplication.class, args);
        JobLauncher jobLauncher = context.getBean(JobLauncher.class);
        Job job = context.getBean("importAirportJob", Job.class); // Replace with your actual job's name
        JobExecution jobExecution = jobLauncher.run(job, new JobParameters());
        System.out.println("Job Status : " + jobExecution.getStatus());
        context.close();
    }



    @Override
    public void run(String... args) throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("JobId", String.valueOf(System.currentTimeMillis()))
                .addDate("date", new Date())
                .addLong("time",System.currentTimeMillis()).toJobParameters();

        //JobExecution allExections = jobLauncher.run(processJob, jobParameters);
        //System.out.println("STATUS :: "+allExections.getStatus());

        //JobExecution execution0 = jobLauncher.run(countObjJsonJob, jobParameters);
        //System.out.println("STATUS :: "+execution0.getStatus());

        //JobExecution execution1 = jobLauncher.run(importAirportTypeJob, jobParameters);
        //System.out.println("STATUS :: "+execution1.getStatus());

        //JobExecution execution2 = jobLauncher.run(importAirportJob, jobParameters);
        //System.out.println("STATUS :: "+execution2.getStatus());
    }
}