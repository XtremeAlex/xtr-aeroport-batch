package com.xtremealex.aeroport.batch.writer;


import com.xtremealex.aeroport.batch.service.AirportTypeCacheService;
import com.xtremealex.aeroport.entity.AirportEntity;
import com.xtremealex.aeroport.entity.typological.AirportTypeTypology;
import com.xtremealex.aeroport.repository.AirportRepository;
import com.xtremealex.aeroport.repository.AirportTypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AirportItemWriter implements ItemWriter<AirportEntity> {

    @Autowired
    private AirportRepository airportRepository;

    @Autowired
    private AirportTypeRepository airportTypeRepository;

    //CACHE IN MEMORIA
    @Autowired
    private AirportTypeCacheService airportTypeCache;

    @Override
    public void write(Chunk<? extends AirportEntity> itemsChunk) throws Exception {
        itemsChunk.forEach(airportEntity -> {
            //airportRepository.save(airportEntity);

            String tipoAeroporto = airportEntity.getType();

            //Cerco prima dentro la cache
            AirportTypeTypology cachedAirportType = airportTypeCache.getAirportType(tipoAeroporto);

            //Se dentro la cache non lo trovo provo a cercarlo dentro il db
            if (cachedAirportType == null) {
                cachedAirportType = airportTypeRepository.findByName(tipoAeroporto).orElseGet(() -> {
                    // Se non esiste
                    AirportTypeTypology newType = new AirportTypeTypology();
                    newType.setName(tipoAeroporto);

                    //Aggiungo questo nuovo tipo di Aeroporto anche nella mappa
                    //airportTypeCache.addOrUpdateAirportType(newType.getName(), newType);
                    return newType;
                });
            }
            airportEntity.setAirportType(cachedAirportType);
        });

        // Salvataggio delle entità degli aeroporti
        airportRepository.saveAll(itemsChunk);
    }

}