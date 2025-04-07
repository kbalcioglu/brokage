package com.example.brokage.application.controllers;

import com.example.brokage.application.controllers.mappers.OrderRequestsMapper;
import com.example.brokage.application.controllers.requests.DepositTryRequest;
import com.example.brokage.domain.usecases.admin.DepositTryUseCase;
import com.example.brokage.domain.usecases.admin.ProcessOrderUseCase;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/{customerId}")
public class AdminController {
    private final OrderRequestsMapper orderRequestsMapper;
    private final DepositTryUseCase depositTryUseCase;
    private final ProcessOrderUseCase processOrderUseCase;

    @PostMapping("/deposit")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @Operation(summary = "Deposit TRY to customers assets", description = "Deposit TRY to customers assets")
    public void depositTry(@Validated @RequestBody DepositTryRequest request, @PathVariable long customerId) {
        var orderDto = orderRequestsMapper.depositTryRequestToOrderDto(request, customerId);
        depositTryUseCase.execute(orderDto);
    }

    @PostMapping("/process/{orderId}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @Operation(summary = "Process Order", description = "Process pending customer orders only from BUY & SELL")
    public void processOrder(@PathVariable long customerId, @PathVariable long orderId) {
        processOrderUseCase.execute(orderId);
    }
}
