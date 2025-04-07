package com.example.brokage.domain.models;

import lombok.Builder;

import java.math.BigDecimal;

@Builder(toBuilder = true)
public record AssetDto(Long id,
                       Long customerId,
                       String assetName,
                       BigDecimal usableSize) {
}