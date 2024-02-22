package com.xtremealex.aeroport.batch.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AirportJson {

    private String continent;

    private String coordinates;

    @JsonProperty("elevation_ft")
    private String elevationFt;

    @JsonProperty("gps_code")
    private String gpsCode;

    @JsonProperty("iata_code")
    private String iataCode;

    private String ident;

    @JsonProperty("iso_country")
    private String isoCountry;

    @JsonProperty("iso_region")
    private String isoRegion;

    @JsonProperty("local_code")
    private String localCode;

    private String municipality;

    private String name;

    private String type;
}
