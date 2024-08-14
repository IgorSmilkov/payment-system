package com.system.payment.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.system.payment.dto.MerchantDto;
import com.system.payment.model.Merchant;

@Mapper(componentModel = "spring")
public interface MerchantMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.name", target = "name")
    @Mapping(source = "user.status", target = "status")
    MerchantDto toMerchantDto(Merchant merchant);

}