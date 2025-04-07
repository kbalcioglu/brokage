package com.example.brokage.application.controllers;

import com.example.brokage.application.controllers.mappers.OrderRequestsMapper;
import com.example.brokage.application.controllers.requests.CreateOrderRequest;
import com.example.brokage.application.controllers.responses.GetCustomerOrdersResponse;
import com.example.brokage.domain.usecases.order.CancelOrderUseCase;
import com.example.brokage.domain.usecases.order.CreateOrderUseCase;
import com.example.brokage.domain.usecases.order.GetCustomerOrdersUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/orders/{customerId}")
@RequiredArgsConstructor
public class OrderController {

    private final OrderRequestsMapper orderRequestsMapper;
    private final CreateOrderUseCase createOrderUseCase;
    private final GetCustomerOrdersUseCase getCustomerOrdersUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create Order", description = "Creates new SELL/BUY order")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CUSTOMER')")
    public void createOrder(@RequestBody CreateOrderRequest request,
                            @Parameter(description = "Customer Id", required = true)
                            @PathVariable long customerId) {
        var orderDto = orderRequestsMapper.createOrderRequestToOrderDto(request, customerId);
        createOrderUseCase.execute(orderDto);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CUSTOMER')")
    public GetCustomerOrdersResponse getCustomerOrders(@PathVariable long customerId) {
        var orders = getCustomerOrdersUseCase.execute(customerId);
        return orderRequestsMapper.orderDtoListToGetCustomerOrderResponse(orders);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CUSTOMER')")
    public void cancelOrder(@PathVariable Long id) {
        cancelOrderUseCase.execute(id);
    }
}
