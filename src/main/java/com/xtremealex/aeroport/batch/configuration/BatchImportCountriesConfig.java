package com.xtremealex.aeroport.batch.configuration;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.xtremealex.aeroport.batch.model.AirportJson;
import com.xtremealex.aeroport.batch.model.CountryJson;
import com.xtremealex.aeroport.batch.processor.AirportTypeItemProcessor;
import com.xtremealex.aeroport.batch.processor.CountryItemProcessor;
import com.xtremealex.aeroport.batch.writer.AirportTypeItemWriter;
import com.xtremealex.aeroport.batch.writer.CountryItemWriter;
import com.xtremealex.aeroport.entity.CountryEntity;
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
public class BatchImportCountriesConfig {

    private final CountryItemProcessor countryItemProcessor;
    private final CountryItemWriter countryItemWriter;
    @Value("${resource.dataset.airports.file.path-countries}")
    private String jsonFileCountries;

    public BatchImportCountriesConfig(CountryItemProcessor countryItemProcessor, CountryItemWriter countryItemWriter) {
        this.countryItemProcessor = countryItemProcessor;
        this.countryItemWriter = countryItemWriter;
    }

    //https://docs.spring.io/spring-batch/reference/readers-and-writers/item-reader-writer-implementations.html

    @Bean
    //@Qualifier("synchronizedCountryItemStreamReader")
    public SynchronizedItemStreamReader<CountryJson> synchronizedCountryItemStreamReader() throws IOException {
        JsonItemReader<CountryJson> jsonItemReader = countriesJsonItemReader();

        return new SynchronizedItemStreamReaderBuilder<CountryJson>()
                .delegate(jsonItemReader)
                .build();
    }

    @Bean
    public JsonItemReader<CountryJson> countriesJsonItemReader() throws IOException {

        Resource resource = new ClassPathResource(jsonFileCountries);
        if (!resource.exists()) {
            throw new FileNotFoundException("Impossibile trovare il file: " + resource.getURI().getPath());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        JacksonJsonObjectReader<CountryJson> jsonObjectReader = new JacksonJsonObjectReader<>(CountryJson.class);
        jsonObjectReader.setMapper(objectMapper);

        JsonItemReader<CountryJson>  reader = new JsonItemReaderBuilder<CountryJson>()
                .jsonObjectReader(new JacksonJsonObjectReader<>(CountryJson.class))
                .resource(resource)
                .name("countriesJsonItemReader")
                .saveState(true) //Terrà traccia dell'ultimo elemento letto dal reader,
                .build();

        //saveState -> Al riavvio di un job fallito o interrotto, il reader può riprendere la lettura dal punto esatto in cui si era fermato, evitando così di dover rielaborare elementi già processati precedentemente.

        return reader;
    }
    
    @Qualifier("countryTypeProcessor")
    @Bean
    public ItemProcessor<CountryJson, CountryEntity> countryTypeProcessor() {
        return countryItemProcessor;
    }

    @Qualifier("countryTypeWriter")
    @Bean
    public ItemWriter<CountryEntity> countryTypeWriter() {
        return countryItemWriter;
    }

}