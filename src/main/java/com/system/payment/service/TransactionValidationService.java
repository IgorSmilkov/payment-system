package com.system.payment.service;

import java.util.Map;
import org.springframework.stereotype.Service;
import com.system.payment.model.transaction.Transaction;
import com.system.payment.strategy.TransactionValidator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionValidationService {
    private final Map<Class<? extends Transaction>, TransactionValidator<? extends Transaction>> validators;


    public boolean isValidTransaction(Transaction transaction) {
        TransactionValidator<Transaction> validator =
                (TransactionValidator<Transaction>) validators.get(transaction.getClass());
        return validator.isValid(transaction);
    }
}
