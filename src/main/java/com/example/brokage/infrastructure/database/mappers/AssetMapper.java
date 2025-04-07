package com.example.brokage.infrastructure.database.mappers;

import com.example.brokage.domain.models.AssetDto;
import com.example.brokage.infrastructure.database.entities.AssetEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AssetMapper {

    AssetDto entityToDto(AssetEntity entity);

    AssetEntity dtoToEntity(AssetDto dto);

    List<AssetDto> entityToDto(List<AssetEntity> entities);

    List<AssetEntity> dtoToEntity(List<AssetDto> dtos);
}