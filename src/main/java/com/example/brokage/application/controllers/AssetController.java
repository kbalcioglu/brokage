package com.example.brokage.application.controllers;

import com.example.brokage.application.controllers.mappers.AssetRequestsMapper;
import com.example.brokage.application.controllers.responses.GetCustomerAssetsResponse;
import com.example.brokage.domain.usecases.assets.GetCustomerAssetsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/assets/{customerId}")
@RequiredArgsConstructor
public class AssetController {
    private final AssetRequestsMapper assetRequestsMapper;
    private final GetCustomerAssetsUseCase getCustomerAssetsUseCase;


    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CUSTOMER')")
    public GetCustomerAssetsResponse getCustomerAssets(@PathVariable long customerId) {
        var assets = getCustomerAssetsUseCase.execute(customerId);
        return assetRequestsMapper.assetDtoListToGetCustomerAssetsResponse(assets);
    }
}
