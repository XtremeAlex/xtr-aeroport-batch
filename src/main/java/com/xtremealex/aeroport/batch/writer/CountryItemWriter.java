package com.xtremealex.aeroport.batch.writer;


import com.xtremealex.aeroport.entity.CountryEntity;
import com.xtremealex.aeroport.repository.CountryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CountryItemWriter implements ItemWriter<CountryEntity> {

    @Autowired
    private CountryRepository countryRepository;

    @Override
    public void write(Chunk<? extends CountryEntity> itemsChunk) throws Exception {
        // Salvataggio delle entità degli aeroporti
        countryRepository.saveAll(itemsChunk);
    }

}