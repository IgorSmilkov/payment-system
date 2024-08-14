package com.system.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.system.payment.model.User;

public record MerchantDto(
        Long id,
        String description,
        BigDecimal totalTransactionSum,
        User.UserStatus status,
        Long userId,
        String name,
        LocalDateTime createdDate,
        LocalDateTime modifiedDate
) {
}
