package com.example.brokage.domain.usecases.order;

import com.example.brokage.domain.exceptions.InvalidOrderStatusException;
import com.example.brokage.domain.exceptions.RecordNotFoundException;
import com.example.brokage.domain.models.OrderStatus;
import com.example.brokage.domain.repositories.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class CancelOrderUseCase {

    private final OrderRepository orderRepository;

    @Transactional
    public void execute(long orderId) {
        var orderDto = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RecordNotFoundException("The order with id : %s does not exist".formatted(orderId)));
        if (!orderDto.status().equals(OrderStatus.PENDING)) {
            throw new InvalidOrderStatusException("The order with id : %s is not pending".formatted(orderId));
        }
        var order = orderDto.toBuilder()
                .status(OrderStatus.CANCELED)
                .build();
        orderRepository.save(order);
    }
}
