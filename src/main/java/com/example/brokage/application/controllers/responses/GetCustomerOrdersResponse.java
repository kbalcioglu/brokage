package com.example.brokage.application.controllers.responses;

import com.example.brokage.domain.models.OrderSide;
import com.example.brokage.domain.models.OrderStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record GetCustomerOrdersResponse(List<Order> orders) {
    public record  Order(String assetName,
                         OrderSide orderSide,
                         BigDecimal price,
                         BigDecimal size,
                         OrderStatus status,
                         LocalDateTime createdDate){

    }
}
