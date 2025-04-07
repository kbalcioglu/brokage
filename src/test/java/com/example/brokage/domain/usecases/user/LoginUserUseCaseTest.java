package com.example.brokage.domain.usecases.user;

import com.example.brokage.domain.services.JwtService;
import com.example.brokage.domain.services.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginUserUseCaseTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @InjectMocks
    private LoginUserUseCase loginUserUseCase;

    @Test
    void shouldReturnTokenWhenAuthenticationSucceeds() {
        // Arrange
        String username = "testUser";
        String password = "testPass";
        String expectedToken = "generated.jwt.token";

        UserDetails userDetails = User.builder()
                .username(username)
                .password("encodedPassword")
                .authorities("CUSTOMER")
                .build();

        // Mock authentication
        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        )).thenReturn(mock(Authentication.class));

        // Mock user details
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        // Mock token generation
        when(jwtService.generateToken(userDetails)).thenReturn(expectedToken);

        // Act
        String result = loginUserUseCase.execute(username, password);

        // Assert
        assertEquals(expectedToken, result);
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        verify(userDetailsService).loadUserByUsername(username);
        verify(jwtService).generateToken(userDetails);
    }

    @Test
    void shouldThrowBadCredentialsExceptionWhenAuthenticationFails() {
        // Arrange
        String username = "testUser";
        String password = "wrongPass";

        // Mock authentication to throw exception
        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager)
                .authenticate(new UsernamePasswordAuthenticationToken(username, password));

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> {
            loginUserUseCase.execute(username, password);
        });

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        verifyNoInteractions(userDetailsService);
        verifyNoInteractions(jwtService);
    }

    @Test
    void shouldThrowExceptionWhenUserDetailsServiceFails() {
        // Arrange
        String username = "testUser";
        String password = "testPass";
        String errorMessage = "User not found";

        // Mock successful authentication
        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        )).thenReturn(mock(Authentication.class));

        // Mock user details service to throw exception
        when(userDetailsService.loadUserByUsername(username))
                .thenThrow(new UsernameNotFoundException(errorMessage));

        // Act & Assert
        Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
            loginUserUseCase.execute(username, password);
        });

        assertEquals(errorMessage, exception.getMessage());
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        verify(userDetailsService).loadUserByUsername(username);
        verifyNoInteractions(jwtService);
    }
}