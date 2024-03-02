package com.xtremealex.aeroport.batch.configuration;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.xtremealex.aeroport.batch.model.AirportJson;
import com.xtremealex.aeroport.batch.processor.AirportItemProcessor;
import com.xtremealex.aeroport.batch.processor.AirportTypeItemProcessor;
import com.xtremealex.aeroport.batch.writer.AirportItemWriter;
import com.xtremealex.aeroport.batch.writer.AirportTypeItemWriter;
import com.xtremealex.aeroport.entity.AirportEntity;
import com.xtremealex.aeroport.entity.typological.AirportTypeTypology;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.item.support.builder.SynchronizedItemStreamReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.FileNotFoundException;
import java.io.IOException;

@Configuration
public class BatchImportAirportsTypeConfig {

    private final AirportTypeItemProcessor airportTypeItemProcessor;
    private final AirportTypeItemWriter airportTypeItemWriter;

    @Value("${resource.dataset.airports.file.path-airports}")
    private String jsonFileAirports;


    public BatchImportAirportsTypeConfig(AirportTypeItemProcessor airportTypeItemProcessor, AirportTypeItemWriter airportTypeItemWriter) {
        this.airportTypeItemProcessor = airportTypeItemProcessor;
        this.airportTypeItemWriter = airportTypeItemWriter;
    }

    //https://docs.spring.io/spring-batch/reference/readers-and-writers/item-reader-writer-implementations.html
    @Bean
    @Qualifier("synchronizedAirportTypeJsonItemStreamReader")
    public SynchronizedItemStreamReader<AirportJson> synchronizedAirportTypeJsonItemStreamReader() throws IOException {
        JsonItemReader<AirportJson> jsonItemReader = jsonAirportTypeItemReader();

        return new SynchronizedItemStreamReaderBuilder<AirportJson>()
                .delegate(jsonItemReader)
                .build();
    }

    @Bean
    public JsonItemReader<AirportJson> jsonAirportTypeItemReader() throws IOException {

        Resource resource = new ClassPathResource(jsonFileAirports);
        if (!resource.exists()) {
            throw new FileNotFoundException("Impossibile trovare il file: " + resource.getURI().getPath());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        JacksonJsonObjectReader<AirportJson> jsonObjectReader = new JacksonJsonObjectReader<>(AirportJson.class);
        jsonObjectReader.setMapper(objectMapper);

        JsonItemReader<AirportJson>  reader = new JsonItemReaderBuilder<AirportJson>()
                .jsonObjectReader(new JacksonJsonObjectReader<>(AirportJson.class))
                .resource(resource)
                .name("airportJsonItemReader")
                .saveState(true) //Terrà traccia dell'ultimo elemento letto dal reader,
                .build();

        //saveState -> Al riavvio di un job fallito o interrotto, il reader può riprendere la lettura dal punto esatto in cui si era fermato, evitando così di dover rielaborare elementi già processati precedentemente.

        return reader;
    }

    @Qualifier("airportTypeProcessor")
    @Bean
    public ItemProcessor<AirportJson, AirportTypeTypology> airportTypeProcessor() {
        return airportTypeItemProcessor;
    }

    @Qualifier("airportTypeWriter")
    @Bean
    public ItemWriter<AirportTypeTypology> airportTypeWriter() {
        return airportTypeItemWriter;
    }

}