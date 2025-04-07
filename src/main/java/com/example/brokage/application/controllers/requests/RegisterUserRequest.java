package com.example.brokage.application.controllers.requests;

import com.example.brokage.domain.models.UserType;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterUserRequest(@NotBlank String username,
                                  @NotBlank String password,
                                  @Parameter(description = "User type CUSTOMER / ADMIN", required = true)
                                  @NotNull UserType userType) {
}
