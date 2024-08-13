package com.system.payment.config;

import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.system.payment.model.transaction.AuthorizeTransaction;
import com.system.payment.model.transaction.ChargeTransaction;
import com.system.payment.model.transaction.RefundTransaction;
import com.system.payment.model.transaction.ReversalTransaction;
import com.system.payment.model.transaction.Transaction;
import com.system.payment.strategy.TransactionValidator;
import com.system.payment.strategy.impl.AuthorizeTransactionValidator;
import com.system.payment.strategy.impl.ChargeTransactionValidator;
import com.system.payment.strategy.impl.RefundTransactionValidator;
import com.system.payment.strategy.impl.ReversalTransactionValidator;

@Configuration
public class TransactionValidationConfig {

    @Bean
    public Map<Class<? extends Transaction>, TransactionValidator<? extends Transaction>> transactionValidators(
            AuthorizeTransactionValidator authorizeTransactionValidator,
            ChargeTransactionValidator chargeTransactionValidator,
            RefundTransactionValidator refundTransactionValidator,
            ReversalTransactionValidator reversalTransactionValidator) {
        return Map.of(
                AuthorizeTransaction.class, authorizeTransactionValidator,
                ChargeTransaction.class, chargeTransactionValidator,
                RefundTransaction.class, refundTransactionValidator,
                ReversalTransaction.class, reversalTransactionValidator
        );
    }
}

