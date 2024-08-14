package com.system.payment.dto;

import com.system.payment.model.User;

public record UpdateMerchantDto(
        String description,
        User.UserStatus status,
        String name
) {
}