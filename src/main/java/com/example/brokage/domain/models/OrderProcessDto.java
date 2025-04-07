package com.example.brokage.domain.models;

import lombok.Builder;

@Builder
public record OrderProcessDto(OrderDto orderDto,
                              AssetDto assetDto,
                              AssetDto tryAssetDto) {
}
