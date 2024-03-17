package com.xtremealex.aeroport.mapper;

import com.xtremealex.aeroport.batch.model.CountryJson;
import com.xtremealex.aeroport.entity.CountryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ICountryBatchMapper {
    //ICountryTypologyMapper INSTANCE = Mappers.getMapper(ICountryTypologyMapper.class);

    List<CountryEntity> countryJsonListToEntityList(List<CountryJson> countryJsonList);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "description", ignore = true)
    CountryEntity countryJsonToEntity(CountryJson countryJson);

}
