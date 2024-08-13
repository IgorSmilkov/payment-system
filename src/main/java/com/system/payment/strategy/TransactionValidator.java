package com.system.payment.strategy;

import com.system.payment.model.transaction.Transaction;

public interface TransactionValidator<T extends Transaction> {
    boolean isValid(T transaction);
}
