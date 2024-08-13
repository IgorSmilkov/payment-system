package com.system.payment.util;

import static com.system.payment.exception.PaymentServiceErrorCode.REFERENCE_TRANSACTION_NOT_FOUND;
import java.util.UUID;
import org.springframework.stereotype.Component;
import com.system.payment.exception.PaymentServiceException;
import com.system.payment.model.transaction.Transaction;
import com.system.payment.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TransactionHelper {

    private final TransactionRepository transactionRepository;

    public Transaction findReferenceTransaction(UUID referenceId) {
        if (referenceId == null) {
            throw new IllegalArgumentException("Reference Transaction ID is required.");
        }
        return transactionRepository.findById(referenceId)
                .orElseThrow(() -> new PaymentServiceException(REFERENCE_TRANSACTION_NOT_FOUND));
    }
}
