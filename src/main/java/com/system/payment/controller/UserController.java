package com.system.payment.controller;


import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.system.payment.model.CustomUserDetails;

@RestController
public class UserController {

    @GetMapping("/api/v1/user/info")
    public UserInfo getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String name = userDetails.getName();
        String email = userDetails.getUsername();
        Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return new UserInfo(name, email, roles);
    }

    public record UserInfo(String name, String email, Set<String> roles) {
    }
}
