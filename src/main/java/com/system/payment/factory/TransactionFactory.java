package com.system.payment.factory;

import java.util.Map;
import org.springframework.stereotype.Component;
import com.system.payment.dto.TransactionRequestDto;
import com.system.payment.model.Merchant;
import com.system.payment.model.TransactionType;
import com.system.payment.model.transaction.Transaction;
import com.system.payment.strategy.TransactionCreationStrategy;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TransactionFactory {

    private final Map<TransactionType, TransactionCreationStrategy> strategies;

    public Transaction createTransaction(TransactionRequestDto transactionDTO, Merchant merchant) {
        TransactionCreationStrategy strategy = strategies.get(transactionDTO.type());
        if (strategy == null) {
            throw new IllegalArgumentException("Invalid transaction type");
        }
        return strategy.createTransaction(transactionDTO, merchant);
    }
}