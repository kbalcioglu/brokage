package com.example.brokage.domain.usecases.assets;

import com.example.brokage.domain.models.AssetDto;
import com.example.brokage.domain.repositories.AssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetCustomerAssetsUseCase {
    private final AssetRepository assetRepository;

    public List<AssetDto> execute(long customerId) {
        var assets = assetRepository.findByCustomerId(customerId);
        if (assets.isEmpty()) {
            throw new RuntimeException("No assets found for customer id " + customerId);
        }
        return assets;
    }
}
