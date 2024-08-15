package com.system.payment.exception;

public record ErrorResponse(
        String errorCode,
        String message,
        int status,
        String details) {
}
