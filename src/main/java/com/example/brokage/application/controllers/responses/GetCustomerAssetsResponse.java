package com.example.brokage.application.controllers.responses;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;


@Builder
public record GetCustomerAssetsResponse(List<Asset> assets) {
    public record  Asset(String assetName,
                         BigDecimal usableSize){

    }
}