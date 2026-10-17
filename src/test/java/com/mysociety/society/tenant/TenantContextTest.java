package com.mysociety.society.tenant;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

class TenantContextTest {
    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void derivesTenantOnlyFromValidatedJwtClaim() {
        UUID societyId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Jwt jwt = new Jwt("token", Instant.now(), Instant.now().plusSeconds(60), java.util.Map.of("alg", "HS256"), java.util.Map.of("sub", userId.toString(), "society_id", societyId.toString()));
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
        assertThat(TenantContext.societyId()).isEqualTo(societyId);
        assertThat(TenantContext.userId()).isEqualTo(userId);
    }
}
