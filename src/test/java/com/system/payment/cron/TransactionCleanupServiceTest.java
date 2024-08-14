package com.system.payment.cron;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.system.payment.model.transaction.AuthorizeTransaction;
import com.system.payment.model.transaction.ChargeTransaction;
import com.system.payment.model.transaction.RefundTransaction;
import com.system.payment.model.transaction.Transaction;
import com.system.payment.repository.TransactionRepository;


@ExtendWith(MockitoExtension.class)
public class TransactionCleanupServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionCleanupService transactionCleanupService;

    private Transaction oldAuthorizeTransaction;
    private Transaction oldChargeTransaction;
    private Transaction oldRefundTransaction;

    @BeforeEach
    void setUp() {
        // Setup some transactions older than 1 hour
        oldAuthorizeTransaction = new AuthorizeTransaction();
        oldAuthorizeTransaction.setId(UUID.randomUUID());
        oldAuthorizeTransaction.setCreatedDate(LocalDateTime.now().minusHours(2));

        oldChargeTransaction = new ChargeTransaction();
        oldChargeTransaction.setId(UUID.randomUUID());
        oldChargeTransaction.setCreatedDate(LocalDateTime.now().minusHours(2));
        oldChargeTransaction.setReferenceTransaction(oldAuthorizeTransaction);

        oldRefundTransaction = new RefundTransaction();
        oldRefundTransaction.setId(UUID.randomUUID());
        oldRefundTransaction.setCreatedDate(LocalDateTime.now().minusHours(2));
        oldRefundTransaction.setReferenceTransaction(oldChargeTransaction);

        oldAuthorizeTransaction.setReferencingTransactions(Set.of(oldChargeTransaction));
        oldChargeTransaction.setReferencingTransactions(Set.of(oldRefundTransaction));
    }

    @Test
    void deleteOldTransactions_shouldDeleteTransactionChain_whenAllTransactionsAreOlderThanOneHour() {
        // Arrange
        when(transactionRepository.findRootTransactionsOlderThan(any(LocalDateTime.class)))
                .thenReturn(List.of(oldAuthorizeTransaction));

        // Act
        transactionCleanupService.deleteOldTransactions();

        // Assert
        verify(transactionRepository, times(1)).delete(oldAuthorizeTransaction);
    }

    @Test
    void deleteOldTransactions_shouldNotDelete_whenAnyTransactionIsNewerThanOneHour() {
        // Arrange
        oldRefundTransaction.setCreatedDate(LocalDateTime.now().minusMinutes(30));

        when(transactionRepository.findRootTransactionsOlderThan(any(LocalDateTime.class)))
                .thenReturn(List.of(oldAuthorizeTransaction));

        // Act
        transactionCleanupService.deleteOldTransactions();

        // Assert
        verify(transactionRepository, never()).delete(any(Transaction.class));
    }

    @Test
    void deleteOldTransactions_shouldHandleEmptyListGracefully() {
        // Arrange
        when(transactionRepository.findRootTransactionsOlderThan(any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        // Act
        transactionCleanupService.deleteOldTransactions();

        // Assert
        verify(transactionRepository, never()).delete(any(Transaction.class));
    }

    @Test
    void deleteOldTransactions_shouldDeleteOnlyRootTransaction_whenNoReferencingTransactions() {
        // Arrange: Create a single old transaction without referencing transactions
        Transaction rootTransaction = new AuthorizeTransaction();
        rootTransaction.setId(UUID.randomUUID());
        rootTransaction.setCreatedDate(LocalDateTime.now().minusHours(2));

        when(transactionRepository.findRootTransactionsOlderThan(any(LocalDateTime.class)))
                .thenReturn(List.of(rootTransaction));

        // Act
        transactionCleanupService.deleteOldTransactions();

        // Assert
        verify(transactionRepository, times(1)).delete(rootTransaction);
    }

}