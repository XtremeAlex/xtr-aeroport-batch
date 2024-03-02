package com.xtremealex.aeroport.batch.configuration.step;

import com.xtremealex.aeroport.batch.listeners.ProgressListener;
import com.xtremealex.aeroport.batch.model.AirportJson;
import com.xtremealex.aeroport.batch.model.CountryJson;
import com.xtremealex.aeroport.batch.tasklet.CountAirportsJsonTasklet;
import com.xtremealex.aeroport.batch.tasklet.CountCountriesJsonTasklet;
import com.xtremealex.aeroport.entity.AirportEntity;
import com.xtremealex.aeroport.entity.CountryEntity;
import com.xtremealex.aeroport.entity.typological.AirportTypeTypology;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class StepConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    private final CountAirportsJsonTasklet countAirportsJsonTasklet;

    private final CountCountriesJsonTasklet countCountriesJsonTasklet;

    private final ProgressListener progressListener;

    public StepConfig(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager, CountAirportsJsonTasklet countAirportsJsonTasklet, CountCountriesJsonTasklet countCountriesJsonTasklet, ProgressListener progressListener) {
        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
        this.countAirportsJsonTasklet = countAirportsJsonTasklet;
        this.countCountriesJsonTasklet = countCountriesJsonTasklet;
        this.progressListener = progressListener;
    }

    //Conta gli Aeroporti nel Json in modo da poter gestire le percentuali per gli step1 e step2
    @Bean
    public Step countAirportsJson(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("readAirportsJson", jobRepository)
                .tasklet(countAirportsJsonTasklet, transactionManager)
                .build();
    }

    //Importa Tipologie di Aeroporti
    @Bean
    public Step step1(
            @Qualifier("synchronizedAirportTypeJsonItemStreamReader") SynchronizedItemStreamReader<AirportJson> jsonItemReader,
            @Qualifier("airportTypeProcessor") ItemProcessor<AirportJson, AirportTypeTypology> airportTypeProcessor,
            @Qualifier("airportTypeWriter") ItemWriter<AirportTypeTypology> airportTypeWriter
            ) {
        return new StepBuilder("step1", jobRepository)
                .<AirportJson, AirportTypeTypology>chunk(100, platformTransactionManager)
                .reader(jsonItemReader)
                .processor(airportTypeProcessor)
                .writer(airportTypeWriter)
                .listener(progressListener)
                //.taskExecutor(new SimpleAsyncTaskExecutor()) // Step: [step1] executed in 1s756ms
                .taskExecutor(taskExecutor()) //Step: [step1] executed in 942ms Tempo di esecuzione

                .build();
    }

    //Importa Aeroporti
    @Bean
    public Step step2(
            @Qualifier("synchronizedAirportJsonItemStreamReader") SynchronizedItemStreamReader<AirportJson> jsonItemReader,
            @Qualifier("airportProcessor") ItemProcessor<AirportJson, AirportEntity> airportProcessor,
            @Qualifier("airportWriter") ItemWriter<AirportEntity> airportWriter) {
        return new StepBuilder("step2", jobRepository)
                .<AirportJson, AirportEntity>chunk(1000, platformTransactionManager)
                .reader(jsonItemReader)
                .processor(airportProcessor)
                .writer(airportWriter)
                .listener(progressListener)
                //.taskExecutor(new SimpleAsyncTaskExecutor())
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public Step countCountriesJson(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("readCountriesJson", jobRepository)
                .tasklet(countCountriesJsonTasklet, transactionManager)
                .build();
    }

    @Bean
    public Step step3(
            @Qualifier("synchronizedCountryItemStreamReader") SynchronizedItemStreamReader<CountryJson> jsonItemReader,
            @Qualifier("countryTypeProcessor") ItemProcessor<CountryJson, CountryEntity> countryTypeProcessor,
            @Qualifier("countryTypeWriter") ItemWriter<CountryEntity> countryTypeWriter
    ) {
        return new StepBuilder("step3", jobRepository)
                .<CountryJson, CountryEntity> chunk(100, platformTransactionManager)
                .reader(jsonItemReader)
                .processor(countryTypeProcessor)
                .writer(countryTypeWriter)
                .listener(progressListener)
                //.taskExecutor(new SimpleAsyncTaskExecutor()) // Step: [step1] executed in 1s756ms
                .taskExecutor(taskExecutor()) //Step: [step1] executed in 942ms Tempo di esecuzione
                .build();
    }


    //Questa è la configurazione migliore per un processore Apple Silicon M1 4 core Low + 4 core Hight
    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(8); // Numero di thread per l'elaborazione parallela
        executor.setMaxPoolSize(8); // Numero massimo di thread
        executor.setQueueCapacity(10); // Dimensione della coda prima di bloccarla
        executor.setThreadNamePrefix("Batch-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.afterPropertiesSet();
        return executor;
    }

}
