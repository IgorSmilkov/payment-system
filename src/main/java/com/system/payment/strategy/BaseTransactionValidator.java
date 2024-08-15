package com.system.payment.strategy;

import com.system.payment.model.TransactionStatus;
import com.system.payment.model.transaction.Transaction;

public abstract class BaseTransactionValidator<T extends Transaction> implements TransactionValidator<T> {

    /**
     * Template method that enforces a sequence of validation steps for a transaction.
     * <p>
     * This method follows the Template Method design pattern, defining the skeleton of the
     * validation algorithm. It checks the consistency of the merchant associated with the
     * transaction, validates the order of the transaction, and then delegates the remaining
     * validation to subclasses.
     *
     * @param transaction the transaction to be validated.
     * @return {@code true} if the transaction is valid according to all validation steps,
     * {@code false} otherwise.
     */
    @Override
    public final boolean isValid(T transaction) {
        if (!isMerchantConsistent(transaction)) {
            return false;
        }
        if (!isTransactionOrderValid(transaction)) {
            return false;
        }
        return isSpecificValid(transaction);
    }

    protected boolean isMerchantConsistent(T transaction) {
        Transaction referenceTransaction = transaction.getReferenceTransaction();
        return referenceTransaction == null || referenceTransaction.getMerchant().getId()
                .equals(transaction.getMerchant().getId());
    }

    protected abstract boolean isTransactionOrderValid(T transaction);

    protected abstract boolean isSpecificValid(T transaction);

    protected boolean isReferenceTransactionValid(Transaction referenceTransaction, TransactionStatus requiredStatus) {
        return referenceTransaction != null && referenceTransaction.getStatus() == requiredStatus;
    }

    protected boolean isTransactionAlreadyReferenced(Transaction referenceTransaction) {
        return referenceTransaction.getReferencingTransactions().stream()
                .anyMatch(t -> t.getStatus() != TransactionStatus.ERROR);
    }
}

