package com.example.brokage.domain.repositories;

import com.example.brokage.domain.models.UserDto;

import java.util.Optional;

public interface UserRepository {

    Optional<UserDto> findByUsername(String username);

    UserDto save(UserDto userDto);

    void delete(String username);
}
