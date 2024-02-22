package com.xtremealex.aeroport.batch.listeners;

import com.xtremealex.aeroport.batch.service.ProgressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProgressListener implements ChunkListener {

    @Autowired
    private ProgressService progressService;

    @Override
    public void afterChunk(ChunkContext context) {
        StepContext stepContext = context.getStepContext();
        String jobName = stepContext.getStepExecution().getJobExecution().getJobInstance().getJobName();
        Integer totalRecordsInt = (Integer) stepContext.getJobExecutionContext().get("totalAirports");
        Long totalRecords = totalRecordsInt != null ? totalRecordsInt.longValue() : null;
        long processedRecords = stepContext.getStepExecution().getReadCount();

        if (totalRecords != null) {
            double percentage = (double) processedRecords / totalRecords * 100;
            progressService.sendProgressUpdate(jobName, percentage); // Modificato per includere il jobName
            log.debug(String.format("%s Progresso: %.2f%% completato", jobName, percentage));
        } else {
            log.warn("totalRecords è null, impossibile calcolare la percentuale di progresso per %s.", jobName);
        }
    }
}
