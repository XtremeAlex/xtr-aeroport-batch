package com.xtremealex.aeroport.batch.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CountryJson {

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Code")
    private String isoCountry;
}
