package com.example.brokage.application.controllers.requests;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DepositTryRequest(@NotNull BigDecimal size) {
}
