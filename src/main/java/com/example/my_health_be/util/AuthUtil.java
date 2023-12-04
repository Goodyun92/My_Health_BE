package com.example.my_health_be.util;

import com.example.my_health_be.exception.AppException;
import com.example.my_health_be.domain.enums.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class AuthUtil {
    public static String getAuthUser() {
        try {
            SecurityContext context = SecurityContextHolder.getContext();

            Authentication authentication = context.getAuthentication();

            Object details = authentication.getPrincipal();

            if (details instanceof UserDetails) {
                return ((UserDetails) details).getUsername();
            }
            return details != null ? details.toString() : null;
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNAUTHORIZED,  "Get Auth User");
        }
    }
}
