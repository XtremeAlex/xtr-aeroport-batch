package com.xtremealex.aeroport.mapper;

import com.xtremealex.aeroport.batch.model.AirportJson;
import com.xtremealex.aeroport.entity.typological.AirportTypeTypology;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IAirportTypeTypologyMapper {

    //IAirportTypeTypologyMapper INSTANCE = Mappers.getMapper(IAirportTypeTypologyMapper.class);
    List<AirportTypeTypology> countryJsonListToEntityList(List<AirportJson> airportJsonList);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    AirportTypeTypology countryJsonToEntity(AirportJson airportJson);

}
