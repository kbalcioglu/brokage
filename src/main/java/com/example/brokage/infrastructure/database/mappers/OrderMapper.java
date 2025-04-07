package com.example.brokage.infrastructure.database.mappers;

import com.example.brokage.domain.models.OrderDto;
import com.example.brokage.infrastructure.database.entities.OrderEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderDto entityToDto(OrderEntity entity);

    OrderEntity dtoToEntity(OrderDto dto);

    List<OrderDto> entityToDto(List<OrderEntity> entities);

    List<OrderEntity> dtoToEntity(List<OrderDto> dtos);
}
