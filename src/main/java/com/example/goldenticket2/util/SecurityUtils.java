package com.example.goldenticket2.util;

import com.example.goldenticket2.entity.User;
import com.example.goldenticket2.security.SecurityUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static User currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof SecurityUser securityUser)) {
            throw new IllegalStateException("No authenticated user in security context");
        }
        return securityUser.getUser();
    }
}
