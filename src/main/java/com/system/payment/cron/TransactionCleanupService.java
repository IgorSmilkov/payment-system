package com.system.payment.cron;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.system.payment.model.transaction.Transaction;
import com.system.payment.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionCleanupService {

    private final TransactionRepository transactionRepository;

    /**
     * Scheduled job that runs every minute to clean up old transactions from the database.
     * <p>
     * This job identifies "root" transactions that are older than one hour and are no longer referenced by other
     * transactions. If a root transaction and all its associated referencing transactions (child transactions) are
     * older than one hour, the entire chain of transactions is deleted.
     * </p>
     * <p>
     * The job relies on JPA's {@code orphanRemoval=true} setting to ensure that when a root transaction is deleted,
     * all of its referencing transactions are also automatically deleted by the persistence layer. This prevents
     * orphaned records and maintains referential integrity.
     * </p>
     */
    @Transactional
    @Scheduled(cron = "0 * * * * *") // Runs every minute
    public void deleteOldTransactions() {
        LocalDateTime thresholdDateTime = LocalDateTime.now().minusHours(1);
        List<Transaction> oldRootTransactions = transactionRepository.findRootTransactionsOlderThan(thresholdDateTime);

        oldRootTransactions.stream()
                .filter(transaction -> canDeleteTransaction(transaction, thresholdDateTime))
                .forEach(this::deleteTransactionAndLog);
    }

    private boolean canDeleteTransaction(Transaction transaction, LocalDateTime thresholdDateTime) {
        // Return true only if the transaction and all its referencing transactions are older than the threshold
        return transaction.getCreatedDate().isBefore(thresholdDateTime) &&
                transaction.getReferencingTransactions().stream()
                        .allMatch(referencingTransaction -> canDeleteTransaction(referencingTransaction, thresholdDateTime));
    }

    private void deleteTransactionAndLog(Transaction transaction) {
        transactionRepository.delete(transaction);
        log.info("Deleted transaction: ID={}, Type={}, CreatedDate={}, Status={}",
                transaction.getId(), transaction.getClass().getSimpleName(),
                transaction.getCreatedDate(), transaction.getStatus());

        transaction.getReferencingTransactions().forEach(ref ->
                log.info("Also deleted referencing transaction: ID={}, Type={}, CreatedDate={}, Status={}",
                        ref.getId(), ref.getClass().getSimpleName(),
                        ref.getCreatedDate(), ref.getStatus())
        );
    }
}
