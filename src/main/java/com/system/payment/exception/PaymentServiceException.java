package com.system.payment.exception;

import lombok.Getter;

@Getter
public class PaymentServiceException extends RuntimeException {
    private final PaymentServiceErrorCode errorCode;

    public PaymentServiceException(PaymentServiceErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public PaymentServiceException(PaymentServiceErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}
