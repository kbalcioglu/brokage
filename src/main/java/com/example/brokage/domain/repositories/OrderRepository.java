package com.example.brokage.domain.repositories;

import com.example.brokage.domain.models.OrderDto;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    List<OrderDto> findByCustomerId(long customerId);

    Optional<OrderDto> findById(long id);

    void save(OrderDto orderDto);
}
