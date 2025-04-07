package com.example.brokage.domain.repositories;

import com.example.brokage.domain.models.AssetDto;

import java.util.List;
import java.util.Optional;

public interface AssetRepository {

    List<AssetDto> findByCustomerId(long customerId);

     Optional<AssetDto> findByCustomerIdAndAssetName(long customerId, String assetName);

    void save(AssetDto assetDto);
}
