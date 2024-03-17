package com.xtremealex.aeroport.batch.processor;

import com.xtremealex.aeroport.batch.model.AirportJson;
import com.xtremealex.aeroport.entity.AirportEntity;
import com.xtremealex.aeroport.mapper.IAirportBatchMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class AirportItemProcessor implements ItemProcessor<AirportJson, AirportEntity> {

    @Autowired
    private IAirportBatchMapper airportMapper;

    @Override
    public AirportEntity process(AirportJson airportJson) {
        log.debug("Processing 'Number {}'", airportJson);

        AirportEntity airportEntity = airportMapper.countryJsonToEntity(airportJson);
        airportEntity.setName(fixEncoding(airportEntity.getName()));
        airportEntity.setMunicipality(fixEncoding(airportEntity.getMunicipality()));

        return airportEntity;
    }

    public String fixEncoding(String value) {
        return getString(value);
    }

    static String getString(String value) {
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