package com.example.brokage.domain.services;
import com.example.brokage.application.properties.JwtProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServicePublicMethodsTest {

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private JwtService jwtService;

    private final String testUsername = "testUser";
    private final String testPassword = "testPass";

    @Test
    void shouldReturnValidTokenString() {

        when(jwtProperties.getSecretKey()).thenReturn("testSecretKey12345678901234567890123456789012");
        when(jwtProperties.getIssuer()).thenReturn("test-issuer");
        when(jwtProperties.getExpirationMinutes()).thenReturn(30L);

        var userDetails = new User(
                testUsername,
                testPassword,
                Collections.emptyList()
        );
        // Act
        String token = jwtService.generateToken(userDetails);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // Valid JWT has 3 parts
    }

    @Test
    void shouldReturnUsernameFromGeneratedToken() {
        when(jwtProperties.getSecretKey()).thenReturn("testSecretKey12345678901234567890123456789012");
        when(jwtProperties.getIssuer()).thenReturn("test-issuer");
        when(jwtProperties.getExpirationMinutes()).thenReturn(30L);

        var userDetails = new User(
                testUsername,
                testPassword,
                Collections.emptyList()
        );
        // Arrange
        String token = jwtService.generateToken(userDetails);

        // Act
        String extractedUsername = jwtService.extractUsername(token);

        // Assert
        assertEquals(testUsername, extractedUsername);
    }

    @Test
    void shouldReturnTrueForValidToken() {
        when(jwtProperties.getSecretKey()).thenReturn("testSecretKey12345678901234567890123456789012");
        when(jwtProperties.getIssuer()).thenReturn("test-issuer");
        when(jwtProperties.getExpirationMinutes()).thenReturn(30L);

        var userDetails = new User(
                testUsername,
                testPassword,
                Collections.emptyList()
        );
        // Arrange
        String token = jwtService.generateToken(userDetails);

        // Act
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void shouldReturnFalseForDifferentUser() {
        when(jwtProperties.getSecretKey()).thenReturn("testSecretKey12345678901234567890123456789012");
        when(jwtProperties.getIssuer()).thenReturn("test-issuer");
        when(jwtProperties.getExpirationMinutes()).thenReturn(30L);

        var userDetails = new User(
                testUsername,
                testPassword,
                Collections.emptyList()
        );
        // Arrange
        String token = jwtService.generateToken(userDetails);
        UserDetails differentUser = new User(
                "differentUser",
                testPassword,
                Collections.emptyList()
        );

        // Act
        boolean isValid = jwtService.isTokenValid(token, differentUser);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void shouldReturnFalseForExpiredToken() {

        var userDetails = new User(
                testUsername,
                testPassword,
                Collections.emptyList()
        );
        // Arrange
        when(jwtProperties.getSecretKey()).thenReturn("testSecretKey12345678901234567890123456789012");
        when(jwtProperties.getExpirationMinutes()).thenReturn(0L);
        String token = jwtService.generateToken(userDetails);

        // Act
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void shouldReturnFalseForMalformedToken() {
        var userDetails = new User(
                testUsername,
                testPassword,
                Collections.emptyList()
        );
        // Arrange
        String malformedToken = "malformed.token.string";

        // Act
        boolean isValid = jwtService.isTokenValid(malformedToken, userDetails);

        // Assert
        assertFalse(isValid);
    }
}