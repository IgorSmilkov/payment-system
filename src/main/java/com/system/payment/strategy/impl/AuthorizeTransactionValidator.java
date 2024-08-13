package com.system.payment.strategy.impl;

import org.springframework.stereotype.Service;
import com.system.payment.model.transaction.AuthorizeTransaction;
import com.system.payment.strategy.BaseTransactionValidator;

@Service
public class AuthorizeTransactionValidator extends BaseTransactionValidator<AuthorizeTransaction> {

    @Override
    protected boolean isSpecificValid(AuthorizeTransaction transaction) {
        return true;
    }

    @Override
    protected boolean isTransactionOrderValid(AuthorizeTransaction transaction) {
        return transaction.getReferenceTransaction() == null;
    }
}
