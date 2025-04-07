package com.example.brokage.domain.services;

import com.example.brokage.domain.exceptions.UserNotExistsException;
import com.example.brokage.domain.models.UserDto;
import com.example.brokage.domain.models.UserType;
import com.example.brokage.domain.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void shouldReturnUserDetailsWhenUserExists() {
        // Given
        String username = "testUser";
        UserDto mockUser = UserDto.builder()
                .userType(UserType.CUSTOMER)
                .username(username)
                .password("encodedPassword")
                .build();

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(mockUser));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Then
        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(UserType.CUSTOMER.name())));

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void shouldThrowUserNotExistsExceptionWhenUserNotFound() {
        // Given
        String username = "nonExistingUser";

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.empty());

        // When & Then
        UserNotExistsException exception = assertThrows(
                UserNotExistsException.class,
                () -> userDetailsService.loadUserByUsername(username)
        );

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }
}