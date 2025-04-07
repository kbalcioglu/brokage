package com.example.brokage.domain.models;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder(toBuilder = true)
public record OrderDto(Long id,
                       Long customerId,
                       String assetName,
                       OrderSide orderSide,
                       BigDecimal price,
                       BigDecimal size,
                       OrderStatus status,
                       LocalDateTime createdDate) {
}
