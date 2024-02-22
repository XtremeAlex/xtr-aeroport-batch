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
public class MyBatchConfig {

    private final AirportItemProcessor airportItemProcessor;
    private final AirportTypeItemProcessor airportTypeItemProcessor;
    private final AirportItemWriter airportItemWriter;
    private final AirportTypeItemWriter airportTypeItemWriter;

    @Value("${resource.dataset.airports.file.path}")
    private String jsonFile;

    public MyBatchConfig(AirportItemProcessor airportItemProcessor, AirportTypeItemProcessor airportTypeItemProcessor, AirportItemWriter airportItemWriter, AirportTypeItemWriter airportTypeItemWriter) {
        this.airportItemProcessor = airportItemProcessor;
        this.airportTypeItemProcessor = airportTypeItemProcessor;
        this.airportItemWriter = airportItemWriter;
        this.airportTypeItemWriter = airportTypeItemWriter;
    }

    //https://docs.spring.io/spring-batch/reference/readers-and-writers/item-reader-writer-implementations.html
    @Bean
    public SynchronizedItemStreamReader<AirportJson> synchronizedItemStreamReader() throws IOException {
        JsonItemReader<AirportJson> jsonItemReader = jsonItemReader();

        return new SynchronizedItemStreamReaderBuilder<AirportJson>()
                .delegate(jsonItemReader)
                .build();
    }

    @Bean
    public JsonItemReader<AirportJson> jsonItemReader() throws IOException {

        Resource resource = new ClassPathResource(jsonFile);
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

    /*
    @Bean
    public JsonItemReader<AirportJson> jsonItemReader() throws IOException {
        return new JsonItemReaderBuilder<AirportJson>()
                .jsonObjectReader(new JacksonJsonObjectReader<>(AirportJson.class))
                .resource(new ClassPathResource("dataset/airports/world-airport.json"))
                .name("airportJsonItemReader")
                .build();
    }
    */

    @Qualifier("airportTypeProcessor")
    @Bean
    public ItemProcessor<AirportJson, AirportTypeTypology> airportTypeProcessor() {
        return airportTypeItemProcessor;
    }

    @Qualifier("airportProcessor")
    @Bean
    public ItemProcessor<AirportJson, AirportEntity> airportProcessor() {
        return airportItemProcessor;
    }

    @Qualifier("airportTypeWriter")
    @Bean
    public ItemWriter<AirportTypeTypology> airportTypeWriter() {
        return airportTypeItemWriter;
    }

    @Bean
    @Qualifier("airportWriter")
    public ItemWriter<AirportEntity> airportWriter() {
        return airportItemWriter;
    }



}