package com.mysociety.society.config;

import java.util.*;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

final class ClaimAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String claim : List.of("roles", "permissions")) {
            Object value = jwt.getClaim(claim);
            if (value instanceof Collection<?> values)
                values.forEach(v -> authorities.add(new SimpleGrantedAuthority((claim.equals("roles") ? "ROLE_" : "") + v)));
        }
        return authorities;
    }
}
