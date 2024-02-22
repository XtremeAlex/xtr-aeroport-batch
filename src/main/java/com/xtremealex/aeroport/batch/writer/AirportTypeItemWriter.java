package com.xtremealex.aeroport.batch.writer;


import com.xtremealex.aeroport.batch.service.AirportTypeCacheService;
import com.xtremealex.aeroport.entity.typological.AirportTypeTypology;
import com.xtremealex.aeroport.repository.AirportTypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AirportTypeItemWriter implements ItemWriter<AirportTypeTypology> {

    @Autowired
    private AirportTypeRepository airportTypeRepository;

    //CACHE IN MEMORIA
    @Autowired
    private AirportTypeCacheService airportTypeCache;

    @Override
    public void write(Chunk<? extends AirportTypeTypology> items) throws Exception {
        for (AirportTypeTypology item : items) {
            if (item != null) {
                //Questo è il secondo controllo che aiuta a mitigare il problema dei duplicati.
                synchronized (this) {
                    if (!airportTypeCache.containsAirportType(item.getName())) {
                        airportTypeRepository.save(item);
                        airportTypeCache.addOrUpdateAirportType(item.getName(), item);
                    }
                }
            }
        }
    }
}
