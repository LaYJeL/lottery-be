package com.game.lottery.service;

import com.game.lottery.config.CacheConfig;
import com.game.lottery.model.User;
import com.game.lottery.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = { com.game.lottery.LotteryApplication.class })
public class UserProvisioningCacheTest {

    @Autowired
    private UserProvisioningService userProvisioningService;

    @Autowired
    private CacheManager cacheManager;

    @MockBean
    private UserRepository userRepository;

    @Test
    void ensureUserExists_shouldCacheResult() {
        String sub = "keycloak-sub-123";
        UUID userId = UUID.randomUUID();

        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn(sub);
        when(jwt.getClaimAsString("email")).thenReturn("test@test.com");

        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setProfile(com.game.lottery.model.UserProfile.empty(mockUser));
        mockUser.getProfile().setEmail("test@test.com");
        mockUser.setChecklist(com.game.lottery.model.UserOnboarding.empty(mockUser));
        mockUser.getChecklist().setEmailVerified(true);

        when(userRepository.findByKeycloakSub(sub)).thenReturn(Optional.of(mockUser));

        UUID result1 = userProvisioningService.ensureUserExists(jwt);

        UUID result2 = userProvisioningService.ensureUserExists(jwt);

        assertEquals(userId, result1);
        assertEquals(userId, result2);

        verify(userRepository, times(1)).findByKeycloakSub(sub);

        assertNotNull(cacheManager.getCache(CacheConfig.USERS_BY_SUB_CACHE).get(sub));
    }
}
