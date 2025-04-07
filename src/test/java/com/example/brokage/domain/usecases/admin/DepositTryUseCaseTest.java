package com.example.brokage.domain.usecases.admin;


import com.example.brokage.domain.exceptions.InvalidOrderSideException;
import com.example.brokage.domain.models.AssetDto;
import com.example.brokage.domain.models.OrderDto;
import com.example.brokage.domain.models.OrderProcessDto;
import com.example.brokage.domain.models.OrderStatus;
import com.example.brokage.domain.models.OrderSide;
import com.example.brokage.domain.repositories.AssetRepository;
import com.example.brokage.domain.repositories.OrderRepository;
import com.example.brokage.domain.services.OrderProcessService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DepositTryUseCaseTest {

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderProcessService orderProcessService;

    @InjectMocks
    private DepositTryUseCase depositTryUseCase;

    @Test
    @Transactional
    void shouldDepositSuccessfully() {
        // Arrange
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

        AssetDto tryAsset = AssetDto.builder()
                .customerId(customerId)
                .assetName("TRY")
                .build();
        when(assetRepository.findByCustomerIdAndAssetName(customerId, "TRY")).thenReturn(Optional.of(tryAsset));

        OrderProcessDto orderProcessDto = OrderProcessDto.builder()
                .orderDto(orderDto.toBuilder().status(OrderStatus.MATCHED).build())
                .tryAssetDto(tryAsset)
                .build();
        when(orderProcessService.depositTry(Optional.of(tryAsset), orderDto)).thenReturn(orderProcessDto);

        // Act
        depositTryUseCase.execute(orderDto);

        // Assert
        verify(assetRepository).findByCustomerIdAndAssetName(customerId, "TRY");
        verify(orderProcessService).depositTry(Optional.of(tryAsset), orderDto);
        verify(assetRepository).save(orderProcessDto.tryAssetDto());
        verify(orderRepository).save(orderProcessDto.orderDto());
    }


    @Test
    @Transactional
    void shouldThrowExceptionWhenOrderIsNotDeposit() {
        // Arrange
        long orderId = 1L;
        long customerId = 1L;
        String assetName = "BTC";

        OrderDto orderDto = OrderDto.builder()
                .id(orderId)
                .customerId(customerId)
                .assetName(assetName)
                .status(OrderStatus.MATCHED)
                .build();


        // Act
        InvalidOrderSideException exception = assertThrows(
                InvalidOrderSideException.class,
                () -> depositTryUseCase.execute(orderDto)
        );
        // Assert
        assertEquals("The order is not in order side DEPOSIT", exception.getMessage());
        verifyNoInteractions(assetRepository);
        verifyNoInteractions(orderProcessService);
        verifyNoInteractions(orderRepository);
    }
}
