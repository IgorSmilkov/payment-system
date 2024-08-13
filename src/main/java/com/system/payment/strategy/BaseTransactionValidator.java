package com.system.payment.strategy;

import com.system.payment.model.TransactionStatus;
import com.system.payment.model.transaction.Transaction;

public abstract class BaseTransactionValidator<T extends Transaction> implements TransactionValidator<T> {

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

