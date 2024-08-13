package com.system.payment.strategy;

import com.system.payment.dto.TransactionRequestDto;
import com.system.payment.model.Merchant;
import com.system.payment.model.transaction.Transaction;

public interface TransactionCreationStrategy {
    Transaction createTransaction(TransactionRequestDto transactionDTO, Merchant merchant);
}
