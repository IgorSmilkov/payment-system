package com.system.payment.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.system.payment.model.transaction.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
}
