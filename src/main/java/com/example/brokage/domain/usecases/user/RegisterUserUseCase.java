package com.example.brokage.domain.usecases.user;

import com.example.brokage.domain.exceptions.UserAlreadyExistsException;
import com.example.brokage.domain.models.UserDto;
import com.example.brokage.domain.models.UserType;
import com.example.brokage.domain.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegisterUserUseCase {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDto execute(String username, String password, UserType userType) {
        if (StringUtils.isBlank(username)) {
            throw new IllegalArgumentException("Username cannot be blank");
        }
        if (StringUtils.isBlank(password)) {
            throw new IllegalArgumentException("Password cannot be blank");
        }
        if (userType == null) {
            throw new IllegalArgumentException("User type cannot be null");
        }
        Optional<UserDto> existingUser = userRepository.findByUsername(username);
        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException("User with name : %s already exists!".formatted(username));
        }
        var userDto = UserDto.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .userType(userType).build();
        var user = userRepository.save(userDto);
        return user;
    }
}