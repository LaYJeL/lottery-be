package com.game.lottery.service;

import com.game.lottery.enums.AccountStatus;
import com.game.lottery.enums.AuthenticationProvider;
import com.game.lottery.model.User;
import com.game.lottery.model.UserOnboarding;
import com.game.lottery.model.UserProfile;
import com.game.lottery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProvisioningService {

    private final UserRepository userRepository;

    @Transactional
    public UUID ensureUserExists(Jwt jwt) {
        String sub = jwt.getSubject();

        return userRepository.findByKeycloakSub(sub)
                .map(User::getId)
                .orElseGet(() -> createUserSafe(jwt, sub));
    }

    private UUID createUserSafe(Jwt jwt, String sub) {
        try {
            return createUser(jwt, sub);
        } catch (DataIntegrityViolationException e) {
            log.warn("Race condition for user {}. Fetching existing ID.", sub);
            return userRepository.findByKeycloakSub(sub)
                    .map(User::getId)
                    .orElseThrow(() -> new IllegalStateException("User creation failed", e));
        }
    }

    private UUID createUser(Jwt jwt, String sub) {
        UUID newId = UUID.randomUUID();

        User user = User.builder()
                .id(newId)
                .keycloakSub(sub)
                .authProvider(resolveProvider(jwt))
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        UserProfile profile = UserProfile.empty(user);
        profile.setEmail(jwt.getClaimAsString("email"));
        profile.setFirstName(jwt.getClaimAsString("given_name"));
        profile.setLastName(jwt.getClaimAsString("family_name"));
        profile.setDisplayName(jwt.getClaimAsString("name"));

        UserOnboarding onboarding = UserOnboarding.empty(user);
        Boolean emailVerified = jwt.getClaimAsBoolean("email_verified");
        onboarding.setEmailVerified(Boolean.TRUE.equals(emailVerified));

        onboarding.syncFromProfile(profile);

        user.setProfile(profile);
        user.setChecklist(onboarding);

        userRepository.save(user);
        log.info("Provisioned new user: {}", newId);
        return newId;
    }

    private AuthenticationProvider resolveProvider(Jwt jwt) {
        Object idp = jwt.getClaims().get("idp"); // Або інше поле, залежно від Keycloak
        if (idp != null && idp.toString().toLowerCase().contains("google")) {
            return AuthenticationProvider.GOOGLE;
        }
        return AuthenticationProvider.LOCAL;
    }
}