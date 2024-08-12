package com.system.payment.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.system.payment.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
