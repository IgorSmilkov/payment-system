package com.system.payment.config;

import java.util.EnumMap;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.system.payment.model.TransactionType;
import com.system.payment.strategy.TransactionCreationStrategy;
import com.system.payment.strategy.impl.AuthorizeTransactionCreationStrategy;
import com.system.payment.strategy.impl.ChargeTransactionCreationStrategy;
import com.system.payment.strategy.impl.RefundTransactionCreationStrategy;
import com.system.payment.strategy.impl.ReversalTransactionCreationStrategy;

@Configuration
public class TransactionStrategyConfig {

    @Bean
    public Map<TransactionType, TransactionCreationStrategy> strategies(
            AuthorizeTransactionCreationStrategy authorizeStrategy,
            ChargeTransactionCreationStrategy chargeStrategy,
            RefundTransactionCreationStrategy refundStrategy,
            ReversalTransactionCreationStrategy reversalStrategy) {


        return new EnumMap<>(Map.ofEntries(
                Map.entry(TransactionType.AUTHORIZE, authorizeStrategy),
                Map.entry(TransactionType.CHARGE, chargeStrategy),
                Map.entry(TransactionType.REFUND, refundStrategy),
                Map.entry(TransactionType.REVERSAL, reversalStrategy)
        ));
    }
}