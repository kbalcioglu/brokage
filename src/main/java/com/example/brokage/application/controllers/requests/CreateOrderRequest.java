package com.example.brokage.application.controllers.requests;

import com.example.brokage.domain.models.OrderSide;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateOrderRequest (@NotBlank String assetName,
                                  @NotNull OrderSide orderSide,
                                  @NotNull BigDecimal price,
                                  @NotNull BigDecimal size) {
}