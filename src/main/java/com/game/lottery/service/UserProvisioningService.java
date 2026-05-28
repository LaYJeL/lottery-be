package com.game.lottery.service;

import com.game.lottery.enums.AccountStatus;
import com.game.lottery.enums.AuthenticationProvider;
import com.game.lottery.model.User;
import com.game.lottery.model.UserOnboarding;
import com.game.lottery.model.UserProfile;
import com.game.lottery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.game.lottery.config.CacheConfig;
import com.game.lottery.dto.UserUpdateRequest;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.game.lottery.enums.VerificationLevel.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProvisioningService {

    private final UserRepository userRepository;
    private final org.springframework.context.ApplicationEventPublisher eventPublisher;

    @Transactional
    @Cacheable(value = CacheConfig.USERS_BY_SUB_CACHE, key = "#jwt.subject")
    public UUID ensureUserExists(Jwt jwt) {
        String sub = jwt.getSubject();

        UUID userId = userRepository.findByKeycloakSub(sub)
                .map(u -> {
                    log.info("User {} found in database", u.getId());
                    syncUserData(u, jwt);
                    return u.getId();
                })
                .orElseGet(() -> createUserSafe(jwt, sub));

        eventPublisher.publishEvent(new com.game.lottery.event.TaskActionEvent(this, userId,
                com.game.lottery.enums.TaskActionType.LOGIN, 1));
        return userId;
    }

    private void syncUserData(User user, Jwt jwt) {
        boolean changed = false;
        UserProfile profile = user.getProfile();

        String email = jwt.getClaimAsString("email");
        if (email != null && !email.equals(profile.getEmail())) {
            profile.setEmail(email);
            // If email changed, maybe reset verification?
            // Keycloak usually verifies email before issuing token with email_verified=true
            changed = true;
        }

        // Sync email_verified status from Token
        Boolean emailVerified = jwt.getClaimAsBoolean("email_verified");
        if (emailVerified != null && emailVerified != user.getChecklist().isEmailVerified()) {
            user.getChecklist().setEmailVerified(emailVerified);
            changed = true;
        }

        String givenName = jwt.getClaimAsString("given_name");
        if (givenName != null && !givenName.equals(profile.getFirstName())) {
            profile.setFirstName(givenName);
            changed = true;
        }

        String familyName = jwt.getClaimAsString("family_name");
        if (familyName != null && !familyName.equals(profile.getLastName())) {
            profile.setLastName(familyName);
            changed = true;
        }

        if (changed) {
            userRepository.save(user);
            log.info("Synced user data from Keycloak token for user {}", user.getId());
        }
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
        UUID newId = UUID.fromString(sub);

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

    @Transactional
    public void updateProfile(UUID userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Attempt to update profile for non-existent user {}", userId);
                    return new IllegalStateException("User not found");
                });

        log.info("Updating profile for user {}", userId);

        UserProfile profile = user.getProfile();

        if (request.getFirstName() != null)
            profile.setFirstName(request.getFirstName());
        if (request.getLastName() != null)
            profile.setLastName(request.getLastName());
        if (request.getMiddleName() != null)
            profile.setMiddleName(request.getMiddleName());
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().equals(profile.getPhone())) {
            profile.setPhone(request.getPhoneNumber());
            user.getChecklist().setPhoneVerified(false);
            log.info("User {} changed phone number. Resetting phone verification status.", userId);
        }
        if (request.getCountry() != null)
            profile.setCountry(request.getCountry());
        if (request.getBirthDate() != null)
            profile.setBirthDate(request.getBirthDate());
        if (request.getDisplayName() != null)
            profile.setDisplayName(request.getDisplayName());
        if (request.getEmailNotifications() != null)
            profile.setEmailNotifications(request.getEmailNotifications());

        user.getChecklist().syncFromProfile(profile);
        checkAndPromoteToBasic(user);

        userRepository.save(user);
    }

    private void checkAndPromoteToBasic(User user) {
        if (user.getProfile().getVerificationLevel() == VERIFIED) {
            return;
        }

        UserOnboarding check = user.getChecklist();
        boolean isProfileFilled = check.isFirstNamePresent() && check.isLastNamePresent() && check.isCountryPresent();

        boolean isContactVerified = check.isEmailVerified() || check.isPhoneVerified();

        if (isProfileFilled && isContactVerified) {
            log.info("Promoting user {} to BASIC verification level", user.getId());
            user.getProfile().setVerificationLevel(BASIC);
        }
    }

    @Transactional
    public void markPhoneVerified(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        user.getChecklist().setPhoneVerified(true);
        log.info("Phone verified for user {}", userId);
        checkAndPromoteToBasic(user);
        userRepository.save(user);
    }

    @Transactional
    public void updateEmailLocal(UUID userId, String newEmail) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        user.getProfile().setEmail(newEmail);
        user.getChecklist().setEmailVerified(false);
        userRepository.save(user);
        log.info("Updated local email for user {}", userId);
    }

    @Transactional
    public void deactivateUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        user.setAccountStatus(AccountStatus.DELETED);
        userRepository.save(user);
        log.info("User {} marked as DELETED in local database", userId);
    }
}