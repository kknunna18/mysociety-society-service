package com.mysociety.society.tenant;

import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

public final class TenantContext {
    private TenantContext() {
    }

    public static UUID societyId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof Jwt jwt) || jwt.getClaimAsString("society_id") == null)
            throw new AccessDeniedException("A society_id claim is required");
        try {
            return UUID.fromString(jwt.getClaimAsString("society_id"));
        } catch (IllegalArgumentException e) {
            throw new AccessDeniedException("Invalid society_id claim");
        }
    }

    public static UUID userId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof Jwt jwt)) throw new AccessDeniedException("JWT authentication is required");
        try {
            return UUID.fromString(jwt.getSubject());
        } catch (IllegalArgumentException e) {
            throw new AccessDeniedException("Invalid subject claim");
        }
    }
}
