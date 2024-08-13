package com.system.payment.strategy.impl;

import org.springframework.stereotype.Component;
import com.system.payment.dto.TransactionRequestDto;
import com.system.payment.model.Merchant;
import com.system.payment.model.TransactionStatus;
import com.system.payment.model.transaction.ReversalTransaction;
import com.system.payment.strategy.TransactionCreationStrategy;
import com.system.payment.util.TransactionHelper;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReversalTransactionCreationStrategy implements TransactionCreationStrategy {

    private final TransactionHelper transactionHelper;

    @Override
    public ReversalTransaction createTransaction(TransactionRequestDto transactionDTO, Merchant merchant) {
        ReversalTransaction transaction = new ReversalTransaction();
        transaction.setCustomerEmail(transactionDTO.customerEmail());
        transaction.setCustomerPhone(transactionDTO.customerPhone());
        transaction.setMerchant(merchant);
        transaction.setReferenceTransaction(transactionHelper.findReferenceTransaction(transactionDTO.referenceTransactionId()));
        transaction.setStatus(TransactionStatus.APPROVED);
        return transaction;
    }
}
