package com.game.lottery.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class KeycloakRoleConverterTest {

    private final KeycloakRoleConverter converter = new KeycloakRoleConverter();

    @Test
    void convert_shouldExtractAndPrefixRoles() {
        // Arrange
        Jwt jwt = mock(Jwt.class);
        Map<String, Object> realmAccess = Map.of("roles", List.of("admin", "user"));
        when(jwt.getClaims()).thenReturn(Map.of("realm_access", realmAccess));

        // Act
        Collection<GrantedAuthority> authorities = converter.convert(jwt);

        // Assert
        assertEquals(2, authorities.size());
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_admin")));
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_user")));
    }

    @Test
    void convert_shouldReturnEmpty_whenNoRealmAccess() {
        // Arrange
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaims()).thenReturn(Map.of());

        // Act
        Collection<GrantedAuthority> authorities = converter.convert(jwt);

        // Assert
        assertTrue(authorities.isEmpty());
    }

    @Test
    void convert_shouldReturnEmpty_whenRolesEmpty() {
        // Arrange
        Jwt jwt = mock(Jwt.class);
        Map<String, Object> realmAccess = Map.of("roles", List.of());
        when(jwt.getClaims()).thenReturn(Map.of("realm_access", realmAccess));

        // Act
        Collection<GrantedAuthority> authorities = converter.convert(jwt);

        // Assert
        assertTrue(authorities.isEmpty());
    }
}
