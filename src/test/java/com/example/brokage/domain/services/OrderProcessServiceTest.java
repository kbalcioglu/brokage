package com.example.brokage.domain.services;
import com.example.brokage.domain.exceptions.InsufficientSizeException;
import com.example.brokage.domain.exceptions.RecordNotFoundException;
import com.example.brokage.domain.models.AssetDto;
import com.example.brokage.domain.models.OrderDto;
import com.example.brokage.domain.models.OrderProcessDto;
import com.example.brokage.domain.models.OrderStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class OrderProcessServiceTest {

    @InjectMocks
    private OrderProcessService orderProcessService;

    @Test
    void shouldProcessSuccessfullyWhenCustomerHasEnoughTryBuyAsset() {
        AssetDto customerTryAsset = AssetDto.builder()
                .assetName("TRY")
                .usableSize(BigDecimal.valueOf(1000))
                .customerId(1L)
                .build();

        OrderDto orderDto = OrderDto.builder()
                .assetName("BTC")
                .size(BigDecimal.ONE)
                .price(BigDecimal.valueOf(500))
                .customerId(1L)
                .build();

        OrderProcessDto result = orderProcessService.buyAsset(
                customerTryAsset,
                Optional.empty(),
                orderDto
        );

        assertNotNull(result);
        assertEquals(OrderStatus.MATCHED, result.orderDto().status());
        assertEquals(BigDecimal.valueOf(500), result.tryAssetDto().usableSize());
        assertEquals(BigDecimal.ONE, result.assetDto().usableSize());
    }

    @Test
    void shouldThrowInsufficientSizeExceptionWhenNotEnoughTryBuyAsset() {
        AssetDto customerTryAsset = AssetDto.builder()
                .assetName("TRY")
                .usableSize(BigDecimal.valueOf(100))
                .customerId(1L)
                .build();

        OrderDto orderDto = OrderDto.builder()
                .assetName("BTC")
                .size(BigDecimal.ONE)
                .price(BigDecimal.valueOf(500))
                .customerId(1L)
                .build();

        assertThrows(InsufficientSizeException.class, () ->
                orderProcessService.buyAsset(
                        customerTryAsset,
                        Optional.empty(),
                        orderDto
                )
        );
    }

    @Test
    void shouldProcessSuccessfullyWhenCustomerHasEnoughAssetSellAsset() {
        AssetDto customerTryAsset = AssetDto.builder()
                .assetName("TRY")
                .usableSize(BigDecimal.valueOf(1000))
                .customerId(1L)
                .build();

        AssetDto customerBtcAsset = AssetDto.builder()
                .assetName("BTC")
                .usableSize(BigDecimal.valueOf(2))
                .customerId(1L)
                .build();

        OrderDto orderDto = OrderDto.builder()
                .assetName("BTC")
                .size(BigDecimal.ONE)
                .price(BigDecimal.valueOf(500))
                .customerId(1L)
                .build();

        OrderProcessDto result = orderProcessService.sellAsset(
                customerTryAsset,
                Optional.of(customerBtcAsset),
                orderDto
        );

        assertNotNull(result);
        assertEquals(OrderStatus.MATCHED, result.orderDto().status());
        assertEquals(BigDecimal.valueOf(1500), result.tryAssetDto().usableSize());
        assertEquals(BigDecimal.ONE, result.assetDto().usableSize());
    }

    @Test
    void shouldThrowRecordNotFoundExceptionWhenAssetDoesNotExistSellAsset() {
        AssetDto customerTryAsset = AssetDto.builder()
                .assetName("TRY")
                .usableSize(BigDecimal.valueOf(1000))
                .customerId(1L)
                .build();

        OrderDto orderDto = OrderDto.builder()
                .assetName("BTC")
                .size(BigDecimal.ONE)
                .price(BigDecimal.valueOf(500))
                .customerId(1L)
                .build();

        assertThrows(RecordNotFoundException.class, () ->
                orderProcessService.sellAsset(
                        customerTryAsset,
                        Optional.empty(),
                        orderDto
                )
        );
    }

    @Test
    void shouldThrowInsufficientSizeExceptionWhenNotEnoughAssetSellAsset() {
        AssetDto customerTryAsset = AssetDto.builder()
                .assetName("TRY")
                .usableSize(BigDecimal.valueOf(1000))
                .customerId(1L)
                .build();

        AssetDto customerBtcAsset = AssetDto.builder()
                .assetName("BTC")
                .usableSize(BigDecimal.valueOf(0.5))
                .customerId(1L)
                .build();

        OrderDto orderDto = OrderDto.builder()
                .assetName("BTC")
                .size(BigDecimal.ONE)
                .price(BigDecimal.valueOf(500))
                .customerId(1L)
                .build();

        assertThrows(InsufficientSizeException.class, () ->
                orderProcessService.sellAsset(
                        customerTryAsset,
                        Optional.of(customerBtcAsset),
                        orderDto
                )
        );
    }

    @Test
    void houldCreateNewTryAssetWhenNotExistsDepositTry() {
        OrderDto orderDto = OrderDto.builder()
                .size(BigDecimal.valueOf(1000))
                .customerId(1L)
                .build();

        OrderProcessDto result = orderProcessService.depositTry(
                Optional.empty(),
                orderDto
        );

        assertNotNull(result);
        assertEquals(OrderStatus.MATCHED, result.orderDto().status());
        assertEquals("TRY", result.orderDto().assetName());
        assertEquals(BigDecimal.valueOf(1000), result.tryAssetDto().usableSize());
    }

    @Test
    void shouldUpdateExistingTryAssetDepositTry() {
        AssetDto existingTryAsset = AssetDto.builder()
                .assetName("TRY")
                .usableSize(BigDecimal.valueOf(500))
                .customerId(1L)
                .build();

        OrderDto orderDto = OrderDto.builder()
                .size(BigDecimal.valueOf(1000))
                .customerId(1L)
                .build();

        OrderProcessDto result = orderProcessService.depositTry(
                Optional.of(existingTryAsset),
                orderDto
        );

        assertNotNull(result);
        assertEquals(OrderStatus.MATCHED, result.orderDto().status());
        assertEquals("TRY", result.orderDto().assetName());
        assertEquals(BigDecimal.valueOf(1500), result.tryAssetDto().usableSize());
    }

    @Test
    void shouldUpdateExistingAssetWhenCustomerAlreadyHasSomeBuyAsset() {
        AssetDto customerTryAsset = AssetDto.builder()
                .assetName("TRY")
                .usableSize(BigDecimal.valueOf(1000))
                .customerId(1L)
                .build();

        AssetDto existingBtcAsset = AssetDto.builder()
                .assetName("BTC")
                .usableSize(BigDecimal.valueOf(0.5))
                .customerId(1L)
                .build();

        OrderDto orderDto = OrderDto.builder()
                .assetName("BTC")
                .size(BigDecimal.ONE)
                .price(BigDecimal.valueOf(500))
                .customerId(1L)
                .build();

        OrderProcessDto result = orderProcessService.buyAsset(
                customerTryAsset,
                Optional.of(existingBtcAsset),
                orderDto
        );

        assertNotNull(result);
        assertEquals(OrderStatus.MATCHED, result.orderDto().status());
        assertEquals(BigDecimal.valueOf(500), result.tryAssetDto().usableSize());
        assertEquals(BigDecimal.valueOf(1.5), result.assetDto().usableSize());
    }
}