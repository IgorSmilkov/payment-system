package com.system.payment.exception;

import lombok.Getter;

@Getter
public enum PaymentServiceErrorCode {
    MERCHANT_DOES_NOT_EXIST("MERCHANT_DOES_NOT_EXIST", "Merchant does not exist"),
    MERCHANT_IS_NOT_ACTIVE("MERCHANT_IS_NOT_ACTIVE", "Merchant is not active"),
    MERCHANT_HAS_TRANSACTIONS("MERCHANT_HAS_TRANSACTIONS", "Cannot delete merchant with existing transactions"),

    REFERENCE_TRANSACTION_NOT_FOUND("REFERENCE_TRANSACTION_NOT_FOUND", "Reference transaction not found.");

    private final String key;
    private final String message;

    PaymentServiceErrorCode(String key, String message) {
        this.key = key;
        this.message = message;
    }
}