package com.system.payment.strategy.impl;

import org.springframework.stereotype.Service;
import com.system.payment.model.TransactionStatus;
import com.system.payment.model.transaction.ChargeTransaction;
import com.system.payment.model.transaction.RefundTransaction;
import com.system.payment.model.transaction.Transaction;
import com.system.payment.strategy.BaseTransactionValidator;

@Service
public class RefundTransactionValidator extends BaseTransactionValidator<RefundTransaction> {

    @Override
    protected boolean isSpecificValid(RefundTransaction transaction) {
        Transaction referenceTransaction = transaction.getReferenceTransaction();
        return isReferenceTransactionValid(referenceTransaction, TransactionStatus.APPROVED)
                && transaction.getAmount().compareTo(((ChargeTransaction) referenceTransaction).getAmount()) == 0
                && !isTransactionAlreadyReferenced(referenceTransaction);
    }

    @Override
    protected boolean isTransactionOrderValid(RefundTransaction transaction) {
        Transaction referenceTransaction = transaction.getReferenceTransaction();
        return referenceTransaction instanceof ChargeTransaction;
    }
}

