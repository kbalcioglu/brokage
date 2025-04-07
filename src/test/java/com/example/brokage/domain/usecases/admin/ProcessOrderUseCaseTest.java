package com.example.brokage.domain.usecases.admin;

import com.example.brokage.domain.exceptions.InvalidOrderSideException;
import com.example.brokage.domain.exceptions.InvalidOrderStatusException;
import com.example.brokage.domain.exceptions.RecordNotFoundException;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class ProcessOrderUseCaseTest {

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderProcessService orderProcessService;

    @InjectMocks
    private ProcessOrderUseCase processOrderUseCase;

    @Test
    @Transactional
    void shouldProcessBuyOrderSuccessfully() {
        // Arrange
        long orderId = 1L;
        long customerId = 1L;
        String assetName = "BTC";

        OrderDto orderDto = OrderDto.builder()
                .id(orderId)
                .customerId(customerId)
                .assetName(assetName)
                .status(OrderStatus.PENDING)
                .orderSide(OrderSide.BUY)
                .build();

        AssetDto tryAsset = AssetDto.builder()
                .customerId(customerId)
                .assetName("TRY")
                .build();

        AssetDto orderAsset = AssetDto.builder()
                .customerId(customerId)
                .assetName(assetName)
                .build();

        OrderProcessDto orderProcessDto = OrderProcessDto.builder()
                .orderDto(orderDto.toBuilder().status(OrderStatus.MATCHED).build())
                .tryAssetDto(tryAsset)
                .assetDto(orderAsset)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderDto));
        when(assetRepository.findByCustomerIdAndAssetName(customerId, "TRY")).thenReturn(Optional.of(tryAsset));
        when(assetRepository.findByCustomerIdAndAssetName(customerId, assetName)).thenReturn(Optional.of(orderAsset));
        when(orderProcessService.buyAsset(tryAsset, Optional.of(orderAsset), orderDto)).thenReturn(orderProcessDto);

        // Act
        processOrderUseCase.execute(orderId);

        // Assert
        verify(orderRepository).findById(orderId);
        verify(assetRepository).findByCustomerIdAndAssetName(customerId, "TRY");
        verify(assetRepository).findByCustomerIdAndAssetName(customerId, assetName);
        verify(orderProcessService).buyAsset(tryAsset, Optional.of(orderAsset), orderDto);
        verify(assetRepository).save(orderProcessDto.tryAssetDto());
        verify(assetRepository).save(orderProcessDto.assetDto());
        verify(orderRepository).save(orderProcessDto.orderDto());
    }

    @Test
    @Transactional
    void shouldProcessSellOrderSuccessfully() {
        // Arrange
        long orderId = 1L;
        long customerId = 1L;
        String assetName = "BTC";

        OrderDto orderDto = OrderDto.builder()
                .id(orderId)
                .customerId(customerId)
                .assetName(assetName)
                .status(OrderStatus.PENDING)
                .orderSide(OrderSide.SELL)
                .build();

        AssetDto tryAsset = AssetDto.builder()
                .customerId(customerId)
                .assetName("TRY")
                .build();

        AssetDto orderAsset = AssetDto.builder()
                .customerId(customerId)
                .assetName(assetName)
                .build();

        OrderProcessDto orderProcessDto = OrderProcessDto.builder()
                .orderDto(orderDto.toBuilder().status(OrderStatus.MATCHED).build())
                .tryAssetDto(tryAsset)
                .assetDto(orderAsset)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderDto));
        when(assetRepository.findByCustomerIdAndAssetName(customerId, "TRY")).thenReturn(Optional.of(tryAsset));
        when(assetRepository.findByCustomerIdAndAssetName(customerId, assetName)).thenReturn(Optional.of(orderAsset));
        when(orderProcessService.sellAsset(tryAsset, Optional.of(orderAsset), orderDto)).thenReturn(orderProcessDto);

        // Act
        processOrderUseCase.execute(orderId);

        // Assert
        verify(orderRepository).findById(orderId);
        verify(assetRepository).findByCustomerIdAndAssetName(customerId, "TRY");
        verify(assetRepository).findByCustomerIdAndAssetName(customerId, assetName);
        verify(orderProcessService).sellAsset(tryAsset, Optional.of(orderAsset), orderDto);
        verify(assetRepository).save(orderProcessDto.tryAssetDto());
        verify(assetRepository).save(orderProcessDto.assetDto());
        verify(orderRepository).save(orderProcessDto.orderDto());
    }

    @Test
    @Transactional
    void shouldThrowRecordNotFoundExceptionWhenOrderNotFound() {
        // Arrange
        long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        RecordNotFoundException exception = assertThrows(
                RecordNotFoundException.class,
                () -> processOrderUseCase.execute(orderId)
        );

        assertEquals("The order with id : 1 does not exist", exception.getMessage());
        verify(orderRepository).findById(orderId);
        verifyNoMoreInteractions(orderRepository);
        verifyNoInteractions(assetRepository, orderProcessService);
    }

    @Test
    @Transactional
    void shouldThrowInvalidOrderStatusExceptionWhenOrderNotPending() {
        // Arrange
        long orderId = 1L;
        OrderDto orderDto = OrderDto.builder()
                .id(orderId)
                .status(OrderStatus.MATCHED)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderDto));

        // Act & Assert
        InvalidOrderStatusException exception = assertThrows(
                InvalidOrderStatusException.class,
                () -> processOrderUseCase.execute(orderId)
        );

        assertEquals("The order with id : 1 is in status MATCHED", exception.getMessage());
        verify(orderRepository).findById(orderId);
        verifyNoMoreInteractions(orderRepository);
        verifyNoInteractions(assetRepository, orderProcessService);
    }

    @Test
    @Transactional
    void shouldThrowInvalidOrderSideExceptionWhenOrderSideInvalid() {
        // Arrange
        long orderId = 1L;
        OrderDto orderDto = OrderDto.builder()
                .id(orderId)
                .status(OrderStatus.PENDING)
                .orderSide(OrderSide.DEPOSIT)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderDto));

        // Act & Assert
        InvalidOrderSideException exception = assertThrows(
                InvalidOrderSideException.class,
                () -> processOrderUseCase.execute(orderId)
        );

        assertEquals("The order is not in order side BUY/SELL", exception.getMessage());
        verify(orderRepository).findById(orderId);
        verifyNoMoreInteractions(orderRepository);
        verifyNoInteractions(assetRepository, orderProcessService);
    }

    @Test
    @Transactional
    void shouldThrowRecordNotFoundExceptionWhenTryAssetNotFound() {
        // Arrange
        long orderId = 1L;
        long customerId = 1L;

        OrderDto orderDto = OrderDto.builder()
                .id(orderId)
                .customerId(customerId)
                .status(OrderStatus.PENDING)
                .orderSide(OrderSide.BUY)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderDto));

        // Act & Assert
        RecordNotFoundException exception = assertThrows(
                RecordNotFoundException.class,
                () -> processOrderUseCase.execute(orderId)
        );

        assertEquals("The customer TRY asset does not exist", exception.getMessage());
        verify(orderRepository).findById(orderId);
        verify(assetRepository).findByCustomerIdAndAssetName(customerId, "TRY");
        verifyNoInteractions(orderProcessService);
    }

}
