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

    /**
     * Creates a transaction based on the specified type and merchant.
     * <p>
     * This method uses the strategy pattern to delegate the creation of different transaction types
     * (e.g., Authorize, Charge, Refund, Reversal) to specific strategies. It ensures that the appropriate
     * strategy is selected based on the transaction type provided in the DTO.
     *
     * @param transactionDTO the DTO containing transaction details including type and reference transaction.
     * @param merchant       the merchant associated with the transaction.
     * @return the created transaction.
     * @throws IllegalArgumentException if an invalid transaction type is provided.
     */
    public Transaction createTransaction(TransactionRequestDto transactionDTO, Merchant merchant) {
        TransactionCreationStrategy strategy = strategies.get(transactionDTO.type());
        if (strategy == null) {
            throw new IllegalArgumentException("Invalid transaction type");
        }
        return strategy.createTransaction(transactionDTO, merchant);
    }
}