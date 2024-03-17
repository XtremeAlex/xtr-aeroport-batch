package com.xtremealex.aeroport.batch.processor;

import com.xtremealex.aeroport.batch.model.AirportJson;
import com.xtremealex.aeroport.batch.service.AirportTypeCacheService;
import com.xtremealex.aeroport.entity.typological.AirportTypeTypology;
import com.xtremealex.aeroport.repository.AirportTypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AirportTypeItemProcessor implements ItemProcessor<AirportJson, AirportTypeTypology> {
    @Autowired
    private AirportTypeRepository airportTypeRepository;

    @Autowired
    private AirportTypeCacheService airportTypeCache;


    @Override
    public AirportTypeTypology process(AirportJson airportJson) {
        //STAMPA LA PERCENTUALE USANDO ExecutionContext
        String tipoAeroporto = airportJson.getType();
        if (airportTypeCache.containsAirportType(tipoAeroporto) || airportTypeRepository.findByName(tipoAeroporto).isPresent()) {
            // SKIP
            return null;
        } else {
            log.debug("Processing 'Number {}'", airportJson);
            AirportTypeTypology newType = new AirportTypeTypology();
            newType.setName(tipoAeroporto);
            return newType;
        }

    }
}