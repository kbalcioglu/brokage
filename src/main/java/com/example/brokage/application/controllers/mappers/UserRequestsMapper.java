package com.example.brokage.application.controllers.mappers;

import com.example.brokage.application.controllers.responses.RegisterUserResponse;
import com.example.brokage.domain.models.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserRequestsMapper {

    RegisterUserResponse userDtoToRegisterUserResponse(UserDto dto);
}
