package com.system.payment.strategy.impl;

import org.springframework.stereotype.Component;
import com.system.payment.dto.TransactionRequestDto;
import com.system.payment.model.Merchant;
import com.system.payment.model.TransactionStatus;
import com.system.payment.model.transaction.AuthorizeTransaction;
import com.system.payment.strategy.TransactionCreationStrategy;

@Component
public class AuthorizeTransactionCreationStrategy implements TransactionCreationStrategy {

    @Override
    public AuthorizeTransaction createTransaction(TransactionRequestDto transactionDTO, Merchant merchant) {
        AuthorizeTransaction transaction = new AuthorizeTransaction();
        transaction.setAmount(transactionDTO.amount());
        transaction.setCustomerEmail(transactionDTO.customerEmail());
        transaction.setCustomerPhone(transactionDTO.customerPhone());
        transaction.setMerchant(merchant);
        transaction.setStatus(TransactionStatus.APPROVED);
        return transaction;
    }
}
