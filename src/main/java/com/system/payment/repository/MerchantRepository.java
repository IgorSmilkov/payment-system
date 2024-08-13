package com.system.payment.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.system.payment.model.Merchant;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    Optional<Merchant> findByUserEmail(String email);
}
