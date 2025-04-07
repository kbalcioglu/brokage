package com.example.brokage.domain.usecases.order;

import com.example.brokage.domain.exceptions.InvalidOrderSideException;
import com.example.brokage.domain.exceptions.InvalidOrderStatusException;
import com.example.brokage.domain.exceptions.RecordNotFoundException;
import com.example.brokage.domain.models.OrderDto;
import com.example.brokage.domain.models.OrderStatus;
import com.example.brokage.domain.models.OrderSide;
import com.example.brokage.domain.repositories.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CancelOrderUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private CancelOrderUseCase cancelOrderUseCase;

    @Test
    @Transactional
    void shouldCancelOrder() {
        long orderId = 1L;
        long customerId = 1L;
        String assetName = "BTC";

        OrderDto orderDto = OrderDto.builder()
                .id(orderId)
                .customerId(customerId)
                .assetName(assetName)
                .status(OrderStatus.PENDING)
                .orderSide(OrderSide.DEPOSIT)
                .build();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderDto));

        var expected = orderDto.toBuilder()
                .status(OrderStatus.CANCELED)
                .build();

        cancelOrderUseCase.execute(orderId);

        verify(orderRepository).findById(orderId);
        verify(orderRepository).save(expected);
    }

    @Test
    @Transactional
    void shouldThrowInvalidOrderStatusExceptionWhenOrderIsNotPending() {
        long orderId = 1L;
        long customerId = 1L;
        String assetName = "BTC";

        OrderDto orderDto = OrderDto.builder()
                .id(orderId)
                .customerId(customerId)
                .assetName(assetName)
                .status(OrderStatus.MATCHED)
                .orderSide(OrderSide.DEPOSIT)
                .build();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderDto));

        InvalidOrderStatusException exception = assertThrows(
                InvalidOrderStatusException.class,
                () -> cancelOrderUseCase.execute(orderId)
        );

        assertEquals("The order with id : %s is not pending".formatted(orderId), exception.getMessage());
        verify(orderRepository).findById(orderId);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    @Transactional
    void shouldThrowRecordNotFoundExceptionWhenOrderNotFound() {
        long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        RecordNotFoundException exception = assertThrows(
                RecordNotFoundException.class,
                () -> cancelOrderUseCase.execute(orderId)
        );

        assertEquals("The order with id : %s does not exist".formatted(orderId), exception.getMessage());
        verify(orderRepository).findById(orderId);
        verifyNoMoreInteractions(orderRepository);
    }
}
