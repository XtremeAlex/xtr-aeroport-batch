package com.xtremealex.aeroport.mapper;

import com.xtremealex.aeroport.batch.model.AirportJson;
import com.xtremealex.aeroport.entity.AirportEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IAirportMapper {

    //IAirportMapper INSTANCE = Mappers.getMapper(IAirportMapper.class);

    List<AirportEntity> countryJsonListToEntityList(List<AirportJson> airportJsonList);

    AirportEntity countryJsonToEntity(AirportJson airportJson);

}
