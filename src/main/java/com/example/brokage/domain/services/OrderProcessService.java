package com.example.brokage.domain.services;

import com.example.brokage.domain.exceptions.InsufficientSizeException;
import com.example.brokage.domain.exceptions.RecordNotFoundException;
import com.example.brokage.domain.models.AssetDto;
import com.example.brokage.domain.models.OrderDto;
import com.example.brokage.domain.models.OrderProcessDto;
import com.example.brokage.domain.models.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderProcessService {

    private static final String TRY_ASSET_NAME = "TRY";

    public OrderProcessDto buyAsset(AssetDto customerTryAsset,
                                    Optional<AssetDto> optionalOrderAsset,
                                    OrderDto orderDto) {
        validateBuyOrder(customerTryAsset, orderDto);

        BigDecimal orderTotalTrySize = calculateOrderTotalValue(orderDto);
        AssetDto updatedTargetAsset = updateTargetAsset(optionalOrderAsset, orderDto);
        AssetDto updatedTryAsset = updateTryAssetForBuy(customerTryAsset, orderTotalTrySize, orderDto);

        return buildOrderProcessResponse(updatedTargetAsset, updatedTryAsset, orderDto);
    }

    public OrderProcessDto sellAsset(AssetDto customerTryAsset,
                                     Optional<AssetDto> optionalOrderAsset,
                                     OrderDto orderDto) {
        AssetDto orderAsset = validateSellOrder(optionalOrderAsset, orderDto);

        BigDecimal orderTotalTrySize = calculateOrderTotalValue(orderDto);
        AssetDto updatedTryAsset = updateTryAssetForSell(customerTryAsset, orderTotalTrySize, orderDto);
        AssetDto updatedOrderAsset = updateOrderAsset(orderAsset, orderDto);

        return buildOrderProcessResponse(updatedOrderAsset, updatedTryAsset, orderDto);
    }

    public OrderProcessDto depositTry(Optional<AssetDto> customerTryAsset,
                                      OrderDto orderDto) {
        AssetDto updatedTryAsset = customerTryAsset
                .map(asset -> updateExistingTryAsset(asset, orderDto))
                .orElseGet(() -> createNewTryAsset(orderDto));

        OrderDto updatedOrder = updateOrderStatusAndAssetName(orderDto, TRY_ASSET_NAME);

        return OrderProcessDto.builder()
                .tryAssetDto(updatedTryAsset)
                .orderDto(updatedOrder)
                .build();
    }

    private void validateBuyOrder(AssetDto customerTryAsset, OrderDto orderDto) {
        BigDecimal orderTotalTrySize = calculateOrderTotalValue(orderDto);
        if (customerTryAsset.usableSize().compareTo(orderTotalTrySize) < 0) {
            throw new InsufficientSizeException(
                    "Insufficient TRY size to buy %s asset".formatted(orderDto.assetName())
            );
        }
    }

    private AssetDto validateSellOrder(Optional<AssetDto> optionalOrderAsset, OrderDto orderDto) {
        AssetDto orderAsset = optionalOrderAsset.orElseThrow(() ->
                new RecordNotFoundException(
                        "The customer %s asset does not exist".formatted(orderDto.assetName())
                ));

        BigDecimal orderTotalTrySize = calculateOrderTotalValue(orderDto);
        if (orderAsset.usableSize().compareTo(orderDto.size()) < 0) {
            throw new InsufficientSizeException(
                    "Insufficient size to sell %s asset".formatted(orderDto.assetName())
            );
        }
        return orderAsset;
    }

    BigDecimal calculateOrderTotalValue(OrderDto orderDto) {
        return orderDto.size().multiply(orderDto.price());
    }

    private AssetDto updateTargetAsset(Optional<AssetDto> optionalAsset, OrderDto orderDto) {
        BigDecimal newUsableSize = optionalAsset
                .map(AssetDto::usableSize)
                .orElse(BigDecimal.ZERO)
                .add(orderDto.size());

        return buildAssetDto(orderDto, newUsableSize);
    }

    private AssetDto updateOrderAsset(AssetDto asset, OrderDto orderDto) {
        BigDecimal newUsableSize = asset.usableSize().subtract(orderDto.size());
        return asset.toBuilder()
                .usableSize(newUsableSize)
                .build();
    }

    private AssetDto updateTryAssetForBuy(AssetDto tryAsset, BigDecimal orderTotalTrySize, OrderDto orderDto) {
        BigDecimal newUsableTrySize = tryAsset.usableSize().subtract(orderTotalTrySize);
        return tryAsset.toBuilder()
                .usableSize(newUsableTrySize)
                .build();
    }

    private AssetDto updateTryAssetForSell(AssetDto tryAsset, BigDecimal orderTotalTrySize, OrderDto orderDto) {
        BigDecimal newUsableTrySize = tryAsset.usableSize().add(orderTotalTrySize);
        return tryAsset.toBuilder()
                .usableSize(newUsableTrySize)
                .build();
    }

    private AssetDto updateExistingTryAsset(AssetDto tryAsset, OrderDto orderDto) {
        return tryAsset.toBuilder()
                .usableSize(tryAsset.usableSize().add(orderDto.size()))
                .build();
    }

    private AssetDto createNewTryAsset(OrderDto orderDto) {
        return AssetDto.builder()
                .assetName(TRY_ASSET_NAME)
                .customerId(orderDto.customerId())
                .usableSize(orderDto.size())
                .build();
    }

    private AssetDto buildAssetDto(OrderDto orderDto, BigDecimal usableSize) {
        return AssetDto.builder()
                .usableSize(usableSize)
                .assetName(orderDto.assetName())
                .customerId(orderDto.customerId())
                .build();
    }

    OrderDto updateOrderStatus(OrderDto orderDto) {
        return orderDto.toBuilder()
                .status(OrderStatus.MATCHED)
                .build();
    }

    private OrderDto updateOrderStatusAndAssetName(OrderDto orderDto, String assetName) {
        return orderDto.toBuilder()
                .status(OrderStatus.MATCHED)
                .assetName(assetName)
                .build();
    }

    private OrderProcessDto buildOrderProcessResponse(AssetDto assetDto,
                                                      AssetDto tryAssetDto,
                                                      OrderDto orderDto) {
        return OrderProcessDto.builder()
                .assetDto(assetDto)
                .tryAssetDto(tryAssetDto)
                .orderDto(updateOrderStatus(orderDto))
                .build();
    }
}