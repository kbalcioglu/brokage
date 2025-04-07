package com.example.brokage.infrastructure.database.mappers;

import com.example.brokage.domain.models.UserDto;
import com.example.brokage.infrastructure.database.entities.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto entityToDto(UserEntity entity);

    UserEntity dtoToEntity(UserDto dto);
}
