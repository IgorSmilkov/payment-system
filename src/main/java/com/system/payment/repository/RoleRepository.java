package com.system.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.system.payment.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
