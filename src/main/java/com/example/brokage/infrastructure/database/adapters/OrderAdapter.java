package com.example.brokage.infrastructure.database.adapters;

import com.example.brokage.domain.models.OrderDto;
import com.example.brokage.domain.repositories.OrderRepository;
import com.example.brokage.infrastructure.database.mappers.OrderMapper;
import com.example.brokage.infrastructure.database.repositories.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrderAdapter implements OrderRepository {

    private final OrderJpaRepository repository;
    private final OrderMapper orderMapper;

    public void save(OrderDto dto) {
        var entity = orderMapper.dtoToEntity(dto);
        repository.save(entity);
    }

    @Override
    public List<OrderDto> findByCustomerId(long customerId) {
        var entities = repository.findByCustomerId(customerId);
        return orderMapper.entityToDto(entities);
    }

    public Optional<OrderDto> findById(long id) {
        return repository.findById(id)
                .map(orderMapper::entityToDto);
    }
}
