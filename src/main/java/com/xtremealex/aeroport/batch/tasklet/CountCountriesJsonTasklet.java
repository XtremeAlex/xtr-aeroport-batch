package com.xtremealex.aeroport.batch.tasklet;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xtremealex.aeroport.batch.model.AirportJson;
import com.xtremealex.aeroport.batch.model.CountryJson;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CountCountriesJsonTasklet implements Tasklet {

    @Value("classpath:${resource.dataset.airports.file.path-countries}")
    private Resource jsonFileResource;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        List<CountryJson> countries = mapper.readValue(jsonFileResource.getInputStream(), new TypeReference<List<CountryJson>>(){});
        int totalCountries = countries.size();
        System.out.println("Total airports: " + totalCountries);

        // Memorizza il conteggio totale nel JobExecutionContext per un utilizzo successivo, es: gestire le percentuali
        chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("totalAirports", totalCountries);

        return RepeatStatus.FINISHED;
    }
}