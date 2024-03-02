package com.xtremealex.aeroport.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "A_D_COUNTRY")
public class CountryEntity implements Serializable {

    private static final long serialVersionUID = 8277781389790520964L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String isoCountry;

    //name del paese in inglese
    private String name;

    private String description;
}
