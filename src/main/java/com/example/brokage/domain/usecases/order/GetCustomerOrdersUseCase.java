package com.example.brokage.domain.usecases.order;

import com.example.brokage.domain.models.OrderDto;
import com.example.brokage.domain.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetCustomerOrdersUseCase {

    private final OrderRepository orderRepository;

    public List<OrderDto> execute(long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }
}
