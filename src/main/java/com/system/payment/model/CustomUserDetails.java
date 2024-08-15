package com.system.payment.model;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import lombok.Getter;

@Getter
public class CustomUserDetails extends org.springframework.security.core.userdetails.User {
    private final Long userId;
    private final String name;

    public CustomUserDetails(User user) {
        super(user.getEmail(), user.getPassword(), user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().name()))
                .toList());
        this.userId = user.getId();
        this.name = user.getName();
    }


    public boolean hasRole(String role) {
        return getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(role));
    }
}

