package com.example.brokage.infrastructure.database.adapters;

import com.example.brokage.domain.models.AssetDto;
import com.example.brokage.domain.repositories.AssetRepository;
import com.example.brokage.infrastructure.database.mappers.AssetMapper;
import com.example.brokage.infrastructure.database.repositories.AssetJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AssetAdapter implements AssetRepository {
    private final AssetJpaRepository repository;
    private final AssetMapper assetMapper;

    @Override
    public List<AssetDto> findByCustomerId(long customerId) {
        var entities = repository.findByCustomerId(customerId);
        return assetMapper.entityToDto(entities);
    }

    @Override
    public Optional<AssetDto> findByCustomerIdAndAssetName(long customerId, String assetName) {
        return repository.findByCustomerIdAndAssetName(customerId, assetName)
                .map(assetMapper::entityToDto);
    }

    public void save(AssetDto dto) {
        var entity = assetMapper.dtoToEntity(dto);
        repository.save(entity);
    }
}
