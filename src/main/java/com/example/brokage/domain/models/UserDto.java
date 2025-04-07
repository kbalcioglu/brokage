package com.example.brokage.domain.models;

import lombok.Builder;

@Builder
public record UserDto(Long id,
                      String username,
                      String password,
                      UserType userType) {
}
