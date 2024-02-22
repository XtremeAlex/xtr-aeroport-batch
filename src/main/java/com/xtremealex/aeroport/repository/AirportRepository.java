package com.xtremealex.aeroport.repository;

import com.xtremealex.aeroport.entity.AirportEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface AirportRepository extends JpaRepository<AirportEntity, Long>, PagingAndSortingRepository<AirportEntity, Long> {

    Page<AirportEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<AirportEntity> findAllByAirportTypeIdIn(Set<Long> airportTypeIds, Pageable pageable);

    Page<AirportEntity> findAllByIsoCountry(String isoCountry, Pageable pageable);

    Page<AirportEntity> findAllByIsoCountryAndAirportTypeIdIn(String isoCountry, Set<Long> airportTypeIds, Pageable pageable);


    Long countAllByNameContainingIgnoreCaseAndAirportTypeIdIn(String name, Set<Long> airportTypeIds);

    Long countAllByIsoCountryAndNameContainingIgnoreCase(String isoCountry, String name);

    Long countAllByIsoCountryAndNameContainingIgnoreCaseAndAirportTypeIdIn(String isoCountry, String name, Set<Long> airportTypeIds);

    Page<AirportEntity> findAllByNameContainingIgnoreCaseAndAirportTypeIdIn(String name, Set<Long> airportTypeIds, Pageable pageable);

    Page<AirportEntity> findAllByIsoCountryAndNameContainingIgnoreCase(String isoCountry, String name, Pageable pageable);

    Page<AirportEntity> findAllByIsoCountryAndNameContainingIgnoreCaseAndAirportTypeIdIn(String isoCountry, String name, Set<Long> airportTypeIds, Pageable pageable);

}
