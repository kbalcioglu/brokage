package com.example.brokage.infrastructure.database.adapters;


import com.example.brokage.domain.models.UserDto;
import com.example.brokage.domain.repositories.UserRepository;
import com.example.brokage.infrastructure.database.mappers.UserMapper;
import com.example.brokage.infrastructure.database.repositories.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserAdapter implements UserRepository {

    private final UserJpaRepository userRepository;
    private final UserMapper userMapper;

    public UserDto save(UserDto userDto) {
        final var entity = userMapper.dtoToEntity(userDto);
        var savedEntity = userRepository.save(entity);
        return userMapper.entityToDto(savedEntity);
    }

    @Override
    public void delete(String username) {
        var entity = userRepository.findByUsername(username);
        entity.ifPresent(userRepository::delete);
    }

    public Optional<UserDto> findByUsername(String username) {
        var entity = userRepository.findByUsername(username);
        if(entity.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(userMapper.entityToDto(entity.get()));
    }
}
