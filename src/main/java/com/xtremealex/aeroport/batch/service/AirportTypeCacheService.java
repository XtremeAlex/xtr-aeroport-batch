package com.xtremealex.aeroport.batch.service;

import com.xtremealex.aeroport.entity.typological.AirportTypeTypology;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
@Slf4j
public class AirportTypeCacheService {

    private final Map<String, AirportTypeTypology> cache = new ConcurrentHashMap<>();

    // Aggiungo oppure aggiorno la tipologia di aeroporto nella cache, bisogna sinconizzarlo,
    // quando più thread tentano di aggiornare la cache si potrebbe finire per avere condizioni che portano a duplicati.
    public synchronized void addOrUpdateAirportType(String typeName, AirportTypeTypology airportType) {
        if (typeName != null && airportType != null) {
            cache.put(typeName, airportType);
        } else {
            log.error("Tentativo di aggiungere un tipo di aeroporto nullo alla cache. : " + airportType);
        }
    }

    // Recupero una tipologia di aeroporto dalla cache tramite il nome
    public AirportTypeTypology getAirportType(String typeName) {
        // Ritorna null se la tipologia non è presente nella cache
        return cache.get(typeName);
    }

    // Esiste nella cache by nome
    public boolean containsAirportType(String typeName) {
        return cache.containsKey(typeName);
    }

    // Rimuove una tipologia di aeroporto dalla cache, non lo uso per adesso
    public void removeAirportType(String typeName) {
        cache.remove(typeName);
    }

    // Rimuove tutto
    public void clearCache() {
        cache.clear();
    }

    public String getAllCache() {
        Set<String> keys = cache.keySet();
        return keys.toString();
    }
}

