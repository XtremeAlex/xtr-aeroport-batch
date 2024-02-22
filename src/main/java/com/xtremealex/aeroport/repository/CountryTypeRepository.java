package com.xtremealex.aeroport.repository;


import com.xtremealex.aeroport.entity.typological.CountryTypology;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CountryTypeRepository extends JpaRepository<CountryTypology, Long> {

    Optional<CountryTypology> findByName(String name);

    Optional<CountryTypology> findByIsoCountry(String iso);

    Page<CountryTypology> findAll(Pageable pageable);


    Optional<CountryTypology> findById(Long id);

}

