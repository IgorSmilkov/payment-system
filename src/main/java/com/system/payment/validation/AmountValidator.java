package com.system.payment.validation;

import java.math.BigDecimal;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AmountValidator implements ConstraintValidator<ValidAmount, BigDecimal> {

    @Override
    public void initialize(ValidAmount constraintAnnotation) {
    }

    @Override
    public boolean isValid(BigDecimal amount, ConstraintValidatorContext context) {
        return amount == null || amount.compareTo(BigDecimal.ZERO) > 0;
    }
}