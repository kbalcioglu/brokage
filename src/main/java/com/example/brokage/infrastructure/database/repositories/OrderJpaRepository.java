package com.example.brokage.infrastructure.database.repositories;

import com.example.brokage.infrastructure.database.entities.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findByCustomerId(long customerId);
}
