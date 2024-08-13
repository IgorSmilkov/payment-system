package com.system.payment.strategy.impl;

import org.springframework.stereotype.Component;
import com.system.payment.dto.TransactionRequestDto;
import com.system.payment.model.Merchant;
import com.system.payment.model.TransactionStatus;
import com.system.payment.model.transaction.ChargeTransaction;
import com.system.payment.strategy.TransactionCreationStrategy;
import com.system.payment.util.TransactionHelper;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChargeTransactionCreationStrategy implements TransactionCreationStrategy {

    private final TransactionHelper transactionHelper;

    @Override
    public ChargeTransaction createTransaction(TransactionRequestDto transactionDTO, Merchant merchant) {
        ChargeTransaction transaction = new ChargeTransaction();
        transaction.setAmount(transactionDTO.amount());
        transaction.setCustomerEmail(transactionDTO.customerEmail());
        transaction.setCustomerPhone(transactionDTO.customerPhone());
        transaction.setMerchant(merchant);
        transaction.setReferenceTransaction(transactionHelper.findReferenceTransaction(transactionDTO.referenceTransactionId()));
        transaction.setStatus(TransactionStatus.APPROVED);
        return transaction;
    }
}