package com.example.brokage.infrastructure.database.repositories;

import com.example.brokage.infrastructure.database.entities.AssetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssetJpaRepository extends JpaRepository<AssetEntity, Long> {

    List<AssetEntity> findByCustomerId(long customerId);

    Optional<AssetEntity> findByCustomerIdAndAssetName(long customerId, String assetName);
}
