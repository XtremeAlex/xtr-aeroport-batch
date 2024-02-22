package com.xtremealex.aeroport.repository;

import com.xtremealex.aeroport.entity.typological.AirportTypeTypology;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AirportTypeRepository extends JpaRepository<AirportTypeTypology, Long> {
    Optional<AirportTypeTypology> findByName(String name);

    Page<AirportTypeTypology> findAll(Pageable pageable);


}
