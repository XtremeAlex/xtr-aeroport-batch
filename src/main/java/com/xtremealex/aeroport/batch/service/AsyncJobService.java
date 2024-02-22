package com.xtremealex.aeroport.batch.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class AsyncJobService {
    @Autowired
    private  JobLauncher jobLauncher;

    @Autowired
    private JobExplorer jobExplorer;

    // direi che è utile fare un check se il job è già in esecuzione...
    @Async
    public JobExecution launchJob(Job job, JobParameters jobParameters) throws Exception {
        Set<JobExecution> runningJobExecutions = jobExplorer.findRunningJobExecutions(job.getName());
        for (JobExecution execution : runningJobExecutions) {
            if (execution.getJobParameters().equals(jobParameters)) {
                return execution;
            }
        }
        return jobLauncher.run(job, jobParameters);
    }
}
