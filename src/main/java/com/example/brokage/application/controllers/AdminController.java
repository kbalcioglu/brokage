package com.example.brokage.application.controllers;

import com.example.brokage.application.controllers.mappers.OrderRequestsMapper;
import com.example.brokage.application.controllers.requests.DepositTryRequest;
import com.example.brokage.domain.usecases.admin.DepositTryUseCase;
import com.example.brokage.domain.usecases.admin.ProcessOrderUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public void depositTry(@RequestBody DepositTryRequest request, @PathVariable long customerId) {
        var orderDto = orderRequestsMapper.depositTryRequestToOrderDto(request, customerId);
        depositTryUseCase.execute(orderDto);
    }

    @PostMapping("/process/{orderId}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public void processOrder(@PathVariable long customerId, @PathVariable long orderId) {
        processOrderUseCase.execute(orderId);
    }
}
