package com.xtremealex.aeroport.mapper;

import com.xtremealex.aeroport.batch.model.AirportJson;
import com.xtremealex.aeroport.entity.AirportEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IAirportMapper {

    //IAirportMapper INSTANCE = Mappers.getMapper(IAirportMapper.class);

    List<AirportEntity> countryJsonListToEntityList(List<AirportJson> airportJsonList);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "airportType", ignore = true)
    AirportEntity countryJsonToEntity(AirportJson airportJson);

}
