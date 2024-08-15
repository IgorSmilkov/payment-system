package com.system.payment.dto;

import java.math.BigDecimal;
import java.util.UUID;
import com.system.payment.model.TransactionType;
import com.system.payment.validation.ValidAmount;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TransactionRequestDto(
        @NotBlank(message = "Customer email cannot be blank")
        @Email(message = "Customer email should be a valid email address")
        String customerEmail,

        String customerPhone,

        @ValidAmount
        BigDecimal amount,

        UUID referenceTransactionId,

        @NotNull(message = "Transaction type cannot be null")
        TransactionType type
) {
}
