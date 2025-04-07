package com.example.brokage.application.controllers.responses;

import com.example.brokage.domain.models.UserType;
import lombok.Builder;

@Builder
public record RegisterUserResponse(Long id,
                                   String username,
                                   UserType userType) {
}
