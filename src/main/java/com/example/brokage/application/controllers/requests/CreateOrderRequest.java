package com.example.brokage.application.controllers.requests;

import com.example.brokage.domain.models.OrderSide;

import java.math.BigDecimal;

public record CreateOrderRequest (String assetName,
                                  OrderSide orderSide,
                                 BigDecimal price,
                                 BigDecimal size) {
}