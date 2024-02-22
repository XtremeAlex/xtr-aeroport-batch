package com.xtremealex.aeroport.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "VOLO")
public class VoloEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    private String numeroVolo;

    //private String compagniaAerea;

    //private String decollo;

    private String destinazione;

    private String tipoAereo;
    private LocalDateTime orarioPartenza;

    //private LocalDateTime orarioArrivo;

}

