package com.xtremealex.aeroport.batch.tasklet;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xtremealex.aeroport.batch.model.AirportJson;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CountObjJsonTasklet implements Tasklet {

    @Value("classpath:${resource.dataset.airports.file.path}")
    private Resource jsonFileResource;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        List<AirportJson> airports = mapper.readValue(jsonFileResource.getInputStream(), new TypeReference<List<AirportJson>>(){});
        int totalAirports = airports.size();
        System.out.println("Total airports: " + totalAirports);

        // Memorizza il conteggio totale nel JobExecutionContext per un utilizzo successivo, es: gestire le percentuali
        chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("totalAirports", totalAirports);

        return RepeatStatus.FINISHED;
    }
}