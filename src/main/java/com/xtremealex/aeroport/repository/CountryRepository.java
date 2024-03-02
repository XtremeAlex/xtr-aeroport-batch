package com.xtremealex.aeroport.repository;


import com.xtremealex.aeroport.entity.CountryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CountryRepository extends JpaRepository<CountryEntity, Long> {

    Optional<CountryEntity> findByName(String name);

    Optional<CountryEntity> findByIsoCountry(String iso);

    Page<CountryEntity> findAll(Pageable pageable);


    Optional<CountryEntity> findById(Long id);

}

