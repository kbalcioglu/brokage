package com.example.brokage.domain.usecases.order;

import com.example.brokage.domain.exceptions.InsufficientSizeException;
import com.example.brokage.domain.exceptions.InvalidOrderSideException;
import com.example.brokage.domain.exceptions.RecordNotFoundException;
import com.example.brokage.domain.models.AssetDto;
import com.example.brokage.domain.models.OrderDto;
import com.example.brokage.domain.models.OrderSide;
import com.example.brokage.domain.models.OrderStatus;
import com.example.brokage.domain.repositories.AssetRepository;
import com.example.brokage.domain.repositories.OrderRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateOrderUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private CreateOrderUseCase createOrderUseCase;

    @Test
    @Transactional
    void shouldCreateBuyOrderSuccessfully() {
        // Arrange
        long customerId = 1L;
        String assetName = "BTC";
        BigDecimal size = BigDecimal.ONE;
        BigDecimal price = BigDecimal.valueOf(50000);
        BigDecimal totalSize = size.multiply(price);

        OrderDto orderDto = OrderDto.builder()
                .customerId(customerId)
                .assetName(assetName)
                .size(size)
                .price(price)
                .orderSide(OrderSide.BUY)
                .build();

        AssetDto tryAsset = AssetDto.builder()
                .customerId(customerId)
                .assetName("TRY")
                .usableSize(totalSize.add(BigDecimal.ONE)) // More than needed
                .build();

        OrderDto expectedSavedOrder = orderDto.toBuilder()
                .status(OrderStatus.PENDING)
                .build();

        when(assetRepository.findByCustomerIdAndAssetName(customerId, "TRY")).thenReturn(Optional.of(tryAsset));
        when(assetRepository.findByCustomerIdAndAssetName(customerId, assetName)).thenReturn(Optional.empty());

        // Act
        createOrderUseCase.execute(orderDto);

        // Assert
        verify(assetRepository).findByCustomerIdAndAssetName(customerId, "TRY");
        verify(assetRepository).findByCustomerIdAndAssetName(customerId, assetName);
        verify(orderRepository).save(expectedSavedOrder);
    }

    @Test
    @Transactional
    void shouldCreateSellOrderSuccessfully() {
        // Arrange
        long customerId = 1L;
        String assetName = "BTC";
        BigDecimal size = BigDecimal.ONE;
        BigDecimal price = BigDecimal.valueOf(50000);
        BigDecimal totalSize = size.multiply(price);

        OrderDto orderDto = OrderDto.builder()
                .customerId(customerId)
                .assetName(assetName)
                .size(size)
                .price(price)
                .orderSide(OrderSide.SELL)
                .build();

        AssetDto tryAsset = AssetDto.builder()
                .customerId(customerId)
                .assetName("TRY")
                .build();

        AssetDto btcAsset = AssetDto.builder()
                .customerId(customerId)
                .assetName(assetName)
                .usableSize(totalSize.add(BigDecimal.ONE)) // More than needed
                .build();

        OrderDto expectedSavedOrder = orderDto.toBuilder()
                .status(OrderStatus.PENDING)
                .build();

        when(assetRepository.findByCustomerIdAndAssetName(customerId, "TRY")).thenReturn(Optional.of(tryAsset));
        when(assetRepository.findByCustomerIdAndAssetName(customerId, assetName)).thenReturn(Optional.of(btcAsset));

        // Act
        createOrderUseCase.execute(orderDto);

        // Assert
        verify(assetRepository).findByCustomerIdAndAssetName(customerId, "TRY");
        verify(assetRepository).findByCustomerIdAndAssetName(customerId, assetName);
        verify(orderRepository).save(expectedSavedOrder);
    }

    @Test
    @Transactional
    void shouldThrowRecordNotFoundExceptionWhenTryAssetNotFound() {
        // Arrange
        OrderDto orderDto = OrderDto.builder()
                .customerId(1L)
                .orderSide(OrderSide.BUY)
                .assetName("assetName")
                .build();
        when(assetRepository.findByCustomerIdAndAssetName(1L, "assetName")).thenReturn(Optional.empty());

        when(assetRepository.findByCustomerIdAndAssetName(1L, "TRY")).thenReturn(Optional.empty());

        // Act & Assert
        RecordNotFoundException exception = assertThrows(
                RecordNotFoundException.class,
                () -> createOrderUseCase.execute(orderDto)
        );

        assertEquals("The customer TRY asset does not exist", exception.getMessage());
        verify(assetRepository).findByCustomerIdAndAssetName(1L, "TRY");
        verifyNoMoreInteractions(assetRepository);
        verifyNoInteractions(orderRepository);
    }

    @Test
    @Transactional
    void shouldThrowInvalidOrderSideExceptionWhenOrderSideInvalid() {
        // Arrange
        OrderDto orderDto = OrderDto.builder()
                .customerId(1L)
                .orderSide(null)
                .assetName("assetName")
                .build();
        when(assetRepository.findByCustomerIdAndAssetName(1L, "assetName")).thenReturn(Optional.empty());

        AssetDto tryAsset = AssetDto.builder().build();
        when(assetRepository.findByCustomerIdAndAssetName(1L, "TRY")).thenReturn(Optional.of(tryAsset));

        // Act & Assert
        InvalidOrderSideException exception = assertThrows(
                InvalidOrderSideException.class,
                () -> createOrderUseCase.execute(orderDto)
        );

        assertEquals("The order is not in order side BUY/SELL", exception.getMessage());
        verify(assetRepository).findByCustomerIdAndAssetName(1L, "TRY");
        verifyNoMoreInteractions(assetRepository);
        verifyNoInteractions(orderRepository);
    }

    @Test
    @Transactional
    void execute_shouldThrowInsufficientSizeExceptionForBuyOrderWhenNotEnoughTry() {
        // Arrange
        long customerId = 1L;
        String assetName = "BTC";
        BigDecimal size = BigDecimal.ONE;
        BigDecimal price = BigDecimal.valueOf(50000);
        BigDecimal totalSize = size.multiply(price);

        OrderDto orderDto = OrderDto.builder()
                .customerId(customerId)
                .assetName(assetName)
                .size(size)
                .price(price)
                .orderSide(OrderSide.BUY)
                .build();

        AssetDto tryAsset = AssetDto.builder()
                .customerId(customerId)
                .assetName("TRY")
                .usableSize(totalSize.subtract(BigDecimal.ONE)) // Less than needed
                .build();

        when(assetRepository.findByCustomerIdAndAssetName(customerId, "TRY")).thenReturn(Optional.of(tryAsset));
        when(assetRepository.findByCustomerIdAndAssetName(customerId, assetName)).thenReturn(Optional.empty());

        // Act & Assert
        InsufficientSizeException exception = assertThrows(
                InsufficientSizeException.class,
                () -> createOrderUseCase.execute(orderDto)
        );

        assertEquals("Insufficient TRY size to buy  BTC asset", exception.getMessage());
        verify(assetRepository).findByCustomerIdAndAssetName(customerId, "TRY");
        verify(assetRepository).findByCustomerIdAndAssetName(customerId, assetName);
        verifyNoInteractions(orderRepository);
    }

    @Test
    @Transactional
    void execute_shouldThrowRecordNotFoundExceptionForSellOrderWhenAssetNotFound() {
        // Arrange
        long customerId = 1L;
        String assetName = "BTC";

        OrderDto orderDto = OrderDto.builder()
                .customerId(customerId)
                .assetName(assetName)
                .size(BigDecimal.ONE)
                .price(BigDecimal.valueOf(50000))
                .orderSide(OrderSide.SELL)
                .build();

        AssetDto tryAsset = AssetDto.builder()
                .customerId(customerId)
                .assetName("TRY")
                .build();

        when(assetRepository.findByCustomerIdAndAssetName(customerId, "TRY")).thenReturn(Optional.of(tryAsset));
        when(assetRepository.findByCustomerIdAndAssetName(customerId, assetName)).thenReturn(Optional.empty());

        // Act & Assert
        RecordNotFoundException exception = assertThrows(
                RecordNotFoundException.class,
                () -> createOrderUseCase.execute(orderDto)
        );

        assertEquals("The customer BTC asset does not exist", exception.getMessage());
        verify(assetRepository).findByCustomerIdAndAssetName(customerId, "TRY");
        verify(assetRepository).findByCustomerIdAndAssetName(customerId, assetName);
        verifyNoInteractions(orderRepository);
    }

    @Test
    @Transactional
    void execute_shouldThrowInsufficientSizeExceptionForSellOrderWhenNotEnoughAsset() {
        // Arrange
        long customerId = 1L;
        String assetName = "BTC";
        BigDecimal size = BigDecimal.ONE;
        BigDecimal price = BigDecimal.valueOf(50000);
        BigDecimal totalSize = size.multiply(price);

        OrderDto orderDto = OrderDto.builder()
                .customerId(customerId)
                .assetName(assetName)
                .size(size)
                .price(price)
                .orderSide(OrderSide.SELL)
                .build();

        AssetDto tryAsset = AssetDto.builder()
                .customerId(customerId)
                .assetName("TRY")
                .build();

        AssetDto btcAsset = AssetDto.builder()
                .customerId(customerId)
                .assetName(assetName)
                .usableSize(totalSize.subtract(BigDecimal.ONE)) // Less than needed
                .build();

        when(assetRepository.findByCustomerIdAndAssetName(customerId, "TRY")).thenReturn(Optional.of(tryAsset));
        when(assetRepository.findByCustomerIdAndAssetName(customerId, assetName)).thenReturn(Optional.of(btcAsset));

        // Act & Assert
        InsufficientSizeException exception = assertThrows(
                InsufficientSizeException.class,
                () -> createOrderUseCase.execute(orderDto)
        );

        assertEquals("Insufficient size to sell BTC asset", exception.getMessage());
        verify(assetRepository).findByCustomerIdAndAssetName(customerId, "TRY");
        verify(assetRepository).findByCustomerIdAndAssetName(customerId, assetName);
        verifyNoInteractions(orderRepository);
    }
}