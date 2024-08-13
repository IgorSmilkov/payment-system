package com.system.payment.dto;

import com.system.payment.model.User;

public record MerchantSummaryDto(
        Long id,
        String name,
        String email,
        User.UserStatus status
) {
}