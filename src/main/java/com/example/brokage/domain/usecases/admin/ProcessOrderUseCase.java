package com.example.brokage.domain.usecases.admin;

import com.example.brokage.domain.exceptions.InvalidOrderSideException;
import com.example.brokage.domain.exceptions.InvalidOrderStatusException;
import com.example.brokage.domain.exceptions.RecordNotFoundException;
import com.example.brokage.domain.models.OrderProcessDto;
import com.example.brokage.domain.models.OrderSide;
import com.example.brokage.domain.models.OrderStatus;
import com.example.brokage.domain.repositories.AssetRepository;
import com.example.brokage.domain.repositories.OrderRepository;
import com.example.brokage.domain.services.OrderProcessService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProcessOrderUseCase {

    private final AssetRepository assetRepository;
    private final OrderRepository orderRepository;
    private final OrderProcessService orderProcessService;

    @Transactional
    public void execute(long orderId) {
        var orderDto = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RecordNotFoundException("The order with id : %s does not exist".formatted(orderId)));
        if (orderDto.status() != OrderStatus.PENDING) {
            throw new InvalidOrderStatusException("The order with id : %s is in status %s"
                    .formatted(orderId, orderDto.status()));
        }

        if (orderDto.orderSide() == null || orderDto.orderSide().equals(OrderSide.DEPOSIT)) {
            throw new InvalidOrderSideException("The order is not in order side BUY/SELL");
        }

        var optionalOrderAsset = assetRepository
                .findByCustomerIdAndAssetName(orderDto.customerId(), orderDto.assetName());

        var customerTryAsset = assetRepository.findByCustomerIdAndAssetName(orderDto.customerId(), "TRY")
                .orElseThrow(() -> new RecordNotFoundException("The customer TRY asset does not exist"));

        OrderProcessDto orderProcessDto;
        if (orderDto.orderSide().equals(OrderSide.BUY)) {
            orderProcessDto = orderProcessService.buyAsset(customerTryAsset, optionalOrderAsset, orderDto);

        } else {
            orderProcessDto = orderProcessService.sellAsset(customerTryAsset, optionalOrderAsset, orderDto);

        }
        assetRepository.save(orderProcessDto.tryAssetDto());
        assetRepository.save(orderProcessDto.assetDto());
        orderRepository.save(orderProcessDto.orderDto());
    }
}
