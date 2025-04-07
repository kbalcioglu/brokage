package com.example.brokage.domain.usecases.user;

import com.example.brokage.domain.models.UserDto;
import com.example.brokage.domain.models.UserType;
import com.example.brokage.domain.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegisterUserUseCase registerUserUseCase;

    @Test
    void execute_shouldRegisterUserSuccessfullyWhenAllConditionsAreMet() {
        // Arrange
        String username = "testUser";
        String password = "testPass123";
        String encodedPassword = "encodedPassword";
        UserType userType = UserType.CUSTOMER;

        UserDto savedUser = UserDto.builder()
                .username(username)
                .password(encodedPassword)
                .userType(userType)
                .build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(userRepository.save(any(UserDto.class))).thenReturn(savedUser);

        // Act
        UserDto result = registerUserUseCase.execute(username, password, userType);

        // Assert
        assertNotNull(result);
        assertEquals(username, result.username());
        assertEquals(encodedPassword, result.password());
        assertEquals(userType, result.userType());

        verify(userRepository).findByUsername(username);
        verify(passwordEncoder).encode(password);
        verify(userRepository).save(any(UserDto.class));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void execute_shouldThrowExceptionWhenUsernameIsBlank(String username) {
        // Arrange
        String password = "testPass123";
        UserType userType = UserType.CUSTOMER;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> registerUserUseCase.execute(username, password, userType)
        );

        assertEquals("Username cannot be blank", exception.getMessage());
        verifyNoInteractions(userRepository, passwordEncoder);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void execute_shouldThrowExceptionWhenPasswordIsBlank(String password) {
        // Arrange
        String username = "testUser";
        UserType userType = UserType.CUSTOMER;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> registerUserUseCase.execute(username, password, userType)
        );

        assertEquals("Password cannot be blank", exception.getMessage());
        verifyNoInteractions(userRepository, passwordEncoder);
    }

    @Test
    void execute_shouldThrowExceptionWhenUserTypeIsNull() {
        // Arrange
        String username = "testUser";
        String password = "testPass123";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> registerUserUseCase.execute(username, password, null)
        );

        assertEquals("User type cannot be null", exception.getMessage());
        verifyNoInteractions(userRepository, passwordEncoder);
    }

    @Test
    void execute_shouldThrowExceptionWhenUserAlreadyExists() {
        // Arrange
        String username = "existingUser";
        String password = "testPass123";
        UserType userType = UserType.CUSTOMER;

        UserDto existingUser = UserDto.builder()
                .username(username)
                .password("oldPassword")
                .userType(userType)
                .build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(existingUser));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> registerUserUseCase.execute(username, password, userType)
        );

        assertEquals("User with name : existingUser already exists!", exception.getMessage());
        verify(userRepository).findByUsername(username);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
    }
}