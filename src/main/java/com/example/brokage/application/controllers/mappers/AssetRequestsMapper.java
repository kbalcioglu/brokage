package com.example.brokage.application.controllers.mappers;

import com.example.brokage.application.controllers.responses.GetCustomerAssetsResponse;
import com.example.brokage.domain.models.AssetDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AssetRequestsMapper {

    GetCustomerAssetsResponse.Asset dtoToGetCustomerAssetsResponse(AssetDto dto);

    List<GetCustomerAssetsResponse.Asset> dtoListToGetCustomerAssetsResponse(List<AssetDto> dtoList);

    default GetCustomerAssetsResponse  assetDtoListToGetCustomerAssetsResponse(List<AssetDto> dtoList){
        return new GetCustomerAssetsResponse(dtoListToGetCustomerAssetsResponse(dtoList));
    }
}
