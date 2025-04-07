package com.example.brokage.application.controllers;

import com.example.brokage.application.controllers.mappers.UserRequestsMapper;
import com.example.brokage.application.controllers.requests.AuthenticationRequest;
import com.example.brokage.application.controllers.requests.RegisterUserRequest;
import com.example.brokage.application.controllers.responses.AuthenticationResponse;
import com.example.brokage.application.controllers.responses.RegisterUserResponse;
import com.example.brokage.domain.usecases.user.LoginUserUseCase;
import com.example.brokage.domain.usecases.user.RegisterUserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final LoginUserUseCase loginUserUseCase;
    private final RegisterUserUseCase registerUserUseCase;
    private final UserRequestsMapper userRequestsMapper;

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Logs in user and gets JWT token")
    public ResponseEntity<AuthenticationResponse> login(@Validated @RequestBody AuthenticationRequest request) {
        String jwtToken = loginUserUseCase.execute(request.username(), request.password());
        return ResponseEntity.ok(new AuthenticationResponse(jwtToken));
    }

    @PostMapping("/register")
    @Operation(summary = "Registers new user", description = "Registers new user. user types CUSTOMER / ADMIN")
    public ResponseEntity<RegisterUserResponse> registerUser(@Validated @RequestBody RegisterUserRequest request) {
        var dto = registerUserUseCase.execute(request.username(), request.password(), request.userType());
        return ResponseEntity.ok(userRequestsMapper.userDtoToRegisterUserResponse(dto));
    }
}