package com.xtremealex.aeroport.mapper;

import com.xtremealex.aeroport.batch.model.CountryJson;
import com.xtremealex.aeroport.entity.typological.CountryTypology;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ICountryTypologyMapper {
    //ICountryTypologyMapper INSTANCE = Mappers.getMapper(ICountryTypologyMapper.class);

    List<CountryTypology> countryJsonListToEntityList(List<CountryJson> countryJsonList);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "description", ignore = true)
    CountryTypology countryJsonToEntity(CountryJson countryJson);

}
