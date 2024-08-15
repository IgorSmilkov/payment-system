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


    /**
     * Validates a transaction using the appropriate validator based on its class type.
     * <p>
     * This method dynamically determines the correct validator for the provided transaction
     * and delegates the validation logic. The method supports different transaction types
     * by leveraging a map of validators keyed by transaction class.
     *
     * @param transaction the transaction to be validated.
     * @return {@code true} if the transaction is valid according to its specific validator,
     * {@code false} otherwise.
     * @throws IllegalArgumentException if no validator is found for the transaction's class.
     */
    public boolean isValidTransaction(Transaction transaction) {
        TransactionValidator<Transaction> validator =
                (TransactionValidator<Transaction>) validators.get(transaction.getClass());
        return validator.isValid(transaction);
    }
}
