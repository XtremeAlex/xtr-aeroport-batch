package com.xtremealex.aeroport.mapper;

import com.xtremealex.aeroport.batch.model.AirportJson;
import com.xtremealex.aeroport.entity.typological.AirportTypeTypology;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IAirportTypeTypologyMapper {

    //IAirportTypeTypologyMapper INSTANCE = Mappers.getMapper(IAirportTypeTypologyMapper.class);
    List<AirportTypeTypology> countryJsonListToEntityList(List<AirportJson> airportJsonList);

    AirportTypeTypology countryJsonToEntity(AirportJson airportJson);

}
