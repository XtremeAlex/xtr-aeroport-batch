package com.xtremealex.aeroport.batch.processor;

import com.xtremealex.aeroport.batch.model.CountryJson;
import com.xtremealex.aeroport.entity.CountryEntity;
import com.xtremealex.aeroport.mapper.ICountryMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class CountryItemProcessor implements ItemProcessor<CountryJson, CountryEntity> {

    @Autowired
    private ICountryMapper countryMapper;

    @Override
    public CountryEntity process(CountryJson countryJson) {
        log.debug("Processing 'Number {}'", countryJson);

        CountryEntity countryEntity = countryMapper.countryJsonToEntity(countryJson);
        return countryEntity;
    }

    public String fixEncoding(String value) {
        if (value == null) {
            return null;
        }
        try {
            byte[] bytes = value.getBytes("ISO-8859-1");
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (UnsupportedEncodingException e) {
            // Gestisci l'eccezione: log, gestisci l'errore o rilancia come un'eccezione a runtime
            e.printStackTrace();
            return value;
        }
    }

}