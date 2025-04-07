package com.example.brokage.domain.usecases.order;

import com.example.brokage.domain.models.OrderDto;
import com.example.brokage.domain.models.OrderStatus;
import com.example.brokage.domain.models.OrderSide;
import com.example.brokage.domain.repositories.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetCustomerOrdersUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private GetCustomerOrdersUseCase getCustomerOrdersUseCase;

    @Test
    void shouldReturnAllCustomerOrdersWhenExists() {
        long customerId = 1L;
        OrderDto orderDto = OrderDto.builder()
                .id(1L)
                .customerId(customerId)
                .assetName("assetName")
                .status(OrderStatus.MATCHED)
                .orderSide(OrderSide.DEPOSIT)
                .build();
        when(orderRepository.findByCustomerId(customerId)).thenReturn(List.of(orderDto));

        var result = getCustomerOrdersUseCase.execute(customerId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderRepository).findByCustomerId(customerId);
    }

    @Test
    void shouldReturnEmptyListWhenNoCustomerOrders() {
        long customerId = 1L;
        when(orderRepository.findByCustomerId(customerId)).thenReturn(List.of());

        var result = getCustomerOrdersUseCase.execute(customerId);

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(orderRepository).findByCustomerId(customerId);
    }
}
