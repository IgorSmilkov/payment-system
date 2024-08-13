package com.system.payment.strategy.impl;

import org.springframework.stereotype.Service;
import com.system.payment.model.TransactionStatus;
import com.system.payment.model.transaction.AuthorizeTransaction;
import com.system.payment.model.transaction.ReversalTransaction;
import com.system.payment.model.transaction.Transaction;
import com.system.payment.strategy.BaseTransactionValidator;

@Service
public class ReversalTransactionValidator extends BaseTransactionValidator<ReversalTransaction> {

    @Override
    protected boolean isSpecificValid(ReversalTransaction transaction) {
        Transaction referenceTransaction = transaction.getReferenceTransaction();
        return isReferenceTransactionValid(referenceTransaction, TransactionStatus.APPROVED)
                && !isTransactionAlreadyReferenced(referenceTransaction);
    }

    @Override
    protected boolean isTransactionOrderValid(ReversalTransaction transaction) {
        Transaction referenceTransaction = transaction.getReferenceTransaction();
        return referenceTransaction instanceof AuthorizeTransaction;
    }
}

