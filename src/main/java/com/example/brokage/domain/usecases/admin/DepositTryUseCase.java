package com.example.brokage.domain.usecases.admin;

import com.example.brokage.domain.exceptions.InvalidOrderSideException;
import com.example.brokage.domain.models.OrderDto;
import com.example.brokage.domain.models.OrderSide;
import com.example.brokage.domain.repositories.AssetRepository;
import com.example.brokage.domain.repositories.OrderRepository;
import com.example.brokage.domain.services.OrderProcessService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DepositTryUseCase {
    private final OrderRepository orderRepository;
    private final AssetRepository assetRepository;
    private final OrderProcessService orderProcessService;

    @Transactional
    public void execute(OrderDto orderDto) {
        if (orderDto.orderSide() == null || !orderDto.orderSide().equals(OrderSide.DEPOSIT)) {
            throw new InvalidOrderSideException("The order is not in order side DEPOSIT");
        }
        var customerTryAsset = assetRepository.findByCustomerIdAndAssetName(orderDto.customerId(), "TRY");
        var orderProcessDto = orderProcessService.depositTry(customerTryAsset, orderDto);
        assetRepository.save(orderProcessDto.tryAssetDto());
        orderRepository.save(orderProcessDto.orderDto());
    }
}
