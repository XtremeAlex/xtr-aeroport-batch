package com.xtremealex.aeroport.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.xtremealex.aeroport.entity.typological.AirportTypeTypology;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "A_D_AIRPORT")
public class AirportEntity implements Serializable {


    private static final long serialVersionUID = 7584884932878712640L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Continente in cui si trova l'aeroporto.
    private String continent;

    //Coordinate geografiche dell'aeroporto, espresse in longitudine e latitudine
    private String coordinates;

    //Altezza dell'aeroporto in piedi sopra il livello del mare
    //Conversione in mt (meters = feet * 0.3048)
    private String elevationFt;

    //Codice GPS dell'aeroporto, un codice alfanumerico univoco assegnato dall'ICAO (Organizzazione Internazionale dell'Aviazione Civile)
    private String gpsCode;

    //Codice IATA dell'aeroporto, un codice alfanumerico di tre lettere utilizzato dalle compagnie aeree, potrebbe non esserci
    private String iataCode;

    //ID dell'aeroporto, un codice alfanumerico univoco utilizzato dalle autorità aeronautiche locali.
    private String ident;

    //Codice ISO del paese in cui si trova l'aeroporto
    private String isoCountry;

    //Codice ISO della regione in cui si trova l'aeroporto
    @JsonProperty("iso_region")
    private String isoRegion;

    //Comune in cui si trova l'aeroporto
    private String municipality;

    //Nome dell'aeroporto.
    private String name;

    //Mi serve per creare mappare la tipologica
    @Transient
    private String type;

    //Tipo di aeroporto. Ad esempio, "heliport" per eliporto o "small_airport" per aeroporto regionale o closed per quelli chiusi.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_AIRPORT_TYPE")
    private AirportTypeTypology airportType;

}
