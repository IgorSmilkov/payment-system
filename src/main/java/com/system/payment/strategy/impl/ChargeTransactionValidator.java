package com.system.payment.strategy.impl;

import org.springframework.stereotype.Service;
import com.system.payment.model.TransactionStatus;
import com.system.payment.model.transaction.AuthorizeTransaction;
import com.system.payment.model.transaction.ChargeTransaction;
import com.system.payment.model.transaction.Transaction;
import com.system.payment.strategy.BaseTransactionValidator;

@Service
public class ChargeTransactionValidator extends BaseTransactionValidator<ChargeTransaction> {

    @Override
    protected boolean isSpecificValid(ChargeTransaction transaction) {
        Transaction referenceTransaction = transaction.getReferenceTransaction();
        return isReferenceTransactionValid(referenceTransaction, TransactionStatus.APPROVED)
                && !isTransactionAlreadyReferenced(referenceTransaction);
    }

    @Override
    protected boolean isTransactionOrderValid(ChargeTransaction transaction) {
        Transaction referenceTransaction = transaction.getReferenceTransaction();
        return referenceTransaction instanceof AuthorizeTransaction;
    }
}
