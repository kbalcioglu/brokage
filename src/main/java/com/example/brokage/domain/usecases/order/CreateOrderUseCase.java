package com.example.brokage.domain.usecases.order;

import com.example.brokage.domain.exceptions.InsufficientSizeException;
import com.example.brokage.domain.exceptions.InvalidOrderSideException;
import com.example.brokage.domain.exceptions.RecordNotFoundException;
import com.example.brokage.domain.models.OrderDto;
import com.example.brokage.domain.models.OrderSide;
import com.example.brokage.domain.models.OrderStatus;
import com.example.brokage.domain.repositories.AssetRepository;
import com.example.brokage.domain.repositories.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class CreateOrderUseCase {

    private final OrderRepository orderRepository;
    private final AssetRepository assetRepository;

    @Transactional
    public void execute(OrderDto orderDto) {
        var optionalOrderAsset = assetRepository
                .findByCustomerIdAndAssetName(orderDto.customerId(), orderDto.assetName());

        var customerTryAsset = assetRepository.findByCustomerIdAndAssetName(orderDto.customerId(), "TRY")
                .orElseThrow(() -> new RecordNotFoundException("The customer TRY asset does not exist"));

        if (orderDto.orderSide()== null || orderDto.orderSide().equals(OrderSide.DEPOSIT)) {
            throw new InvalidOrderSideException("The order is not in order side BUY/SELL");
        }
        var orderTotalSize = orderDto.size().multiply(orderDto.price());
        if (orderDto.orderSide().equals(OrderSide.BUY)) {
            if (customerTryAsset.usableSize().compareTo(orderTotalSize) < 0) {
                throw new InsufficientSizeException("Insufficient TRY size to buy  %s asset".formatted(orderDto.assetName()));
            }
        } else if (orderDto.orderSide().equals(OrderSide.SELL)) {
            if (optionalOrderAsset.isEmpty()) {
                throw new RecordNotFoundException("The customer %s asset does not exist".formatted(orderDto.assetName()));
            }
            if (optionalOrderAsset.get().usableSize().compareTo(orderTotalSize) < 0) {
                throw new InsufficientSizeException("Insufficient size to sell %s asset".formatted(orderDto.assetName()));
            }
        }
        var order = orderDto.toBuilder()
                .status(OrderStatus.PENDING)
                .build();
        orderRepository.save(order);
    }
}
