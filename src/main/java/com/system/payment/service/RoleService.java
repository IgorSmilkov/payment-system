package com.system.payment.service;

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.system.payment.model.Role;
import com.system.payment.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private Map<Role.RoleType, Role> roleCache;


    @PostConstruct
    public void init() {
        roleCache = roleRepository.findAll().stream()
                .collect(Collectors.toMap(
                        Role::getName,
                        role -> role,
                        (existing, replacement) -> existing,
                        () -> new EnumMap<>(Role.RoleType.class)
                ));
    }

    public Role getRoleByType(Role.RoleType roleType) {
        return roleCache.get(roleType);
    }
}
