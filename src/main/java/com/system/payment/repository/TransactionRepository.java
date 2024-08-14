package com.system.payment.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.system.payment.model.transaction.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    @EntityGraph(attributePaths = {"referencingTransactions"})
    @Query("SELECT t FROM Transaction t WHERE t.createdDate < :thresholdDateTime AND t.referenceTransaction IS NULL")
    List<Transaction> findRootTransactionsOlderThan(@Param("thresholdDateTime") LocalDateTime thresholdDateTime);
}
