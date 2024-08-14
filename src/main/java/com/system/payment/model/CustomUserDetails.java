package com.system.payment.model;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class CustomUserDetails extends org.springframework.security.core.userdetails.User {
    private final Long userId;

    public CustomUserDetails(User user) {
        super(user.getEmail(), user.getPassword(), user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().name()))
                .toList());
        this.userId = user.getId();
    }

    public Long getUserId() {
        return userId;
    }

    public boolean hasRole(String role) {
        return getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(role));
    }
}

