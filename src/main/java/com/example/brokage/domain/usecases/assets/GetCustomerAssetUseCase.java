package com.example.brokage.domain.usecases.assets;

import com.example.brokage.domain.exceptions.RecordNotFoundException;
import com.example.brokage.domain.models.AssetDto;
import com.example.brokage.domain.repositories.AssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetCustomerAssetUseCase {
    private final AssetRepository assetRepository;


    public AssetDto execute(long customerId, String assetName) {
        var asset = assetRepository.findByCustomerIdAndAssetName(customerId, assetName);
        if (asset.isEmpty()) {
            throw new RecordNotFoundException("No assets found for customer id " + customerId + " and asset name " + assetName);
        }
        return asset.get();
    }
}
