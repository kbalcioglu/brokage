package com.example.brokage.application.controllers.requests;

import com.example.brokage.domain.models.UserType;

public record RegisterUserRequest(String username,
                                  String password,
                                  UserType userType) {
}
