package com.system.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import com.system.payment.model.TransactionStatus;
import com.system.payment.model.TransactionType;

public record TransactionResponseDto(
        UUID id,

        String customerEmail,

        String customerPhone,

        BigDecimal amount,

        UUID referenceTransactionId,

        TransactionType type,

        TransactionStatus status,

        MerchantSummaryDto merchant,

        LocalDateTime createdDate,

        LocalDateTime modifiedDate
) {
}