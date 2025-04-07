package com.example.brokage.application.controllers.mappers;

import com.example.brokage.application.controllers.requests.CreateOrderRequest;
import com.example.brokage.application.controllers.requests.DepositTryRequest;
import com.example.brokage.application.controllers.responses.GetCustomerOrdersResponse;
import com.example.brokage.domain.models.OrderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderRequestsMapper {

    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "createdDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "customerId", source = "customerId")
    OrderDto createOrderRequestToOrderDto(CreateOrderRequest request, long customerId);

    @Mapping(target = "status", constant = "MATCHED")
    @Mapping(target = "createdDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "customerId", source = "customerId")
    @Mapping(target = "assetName", constant = "TRY")
    @Mapping(target = "orderSide", constant = "DEPOSIT")
    @Mapping(target = "price", constant = "1")
    OrderDto depositTryRequestToOrderDto(DepositTryRequest request, long customerId);


    GetCustomerOrdersResponse.Order dtoToGetCustomerOrderResponse(OrderDto dto);

    List<GetCustomerOrdersResponse.Order> dtoListToGetCustomerOrderResponse(List<OrderDto> dtoList);

    default GetCustomerOrdersResponse orderDtoListToGetCustomerOrderResponse(List<OrderDto> dtoList) {
        return new GetCustomerOrdersResponse(dtoListToGetCustomerOrderResponse(dtoList));
    }
}
