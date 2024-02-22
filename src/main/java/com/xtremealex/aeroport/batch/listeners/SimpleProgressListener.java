package com.xtremealex.aeroport.batch.listeners;


import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SimpleProgressListener implements ChunkListener {

    public void afterChunk(ChunkContext context) {
        StepContext stepContext = context.getStepContext();
        Integer totalRecordsInt = (Integer) stepContext.getJobExecutionContext().get("totalAirports");
        Long totalRecords = totalRecordsInt != null ? totalRecordsInt.longValue() : null;
        long processedRecords = stepContext.getStepExecution().getReadCount();
        if (totalRecords != null) {
            double percentage = (double) processedRecords / totalRecords * 100;
            log.debug(String.format("Progresso: %.2f%% completato", percentage));
        } else {
            log.warn("totalRecords è null, impossibile calcolare la percentuale di progresso.");
        }
    }

}
