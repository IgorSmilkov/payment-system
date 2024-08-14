package com.system.payment.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.system.payment.model.Merchant;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    Optional<Merchant> findByUserEmail(String email);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.merchant.id = :merchantId")
    Long countTransactionsByMerchantId(@Param("merchantId") Long merchantId);
}
