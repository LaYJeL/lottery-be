package com.game.lottery.service;

import com.game.lottery.dto.UserUpdateRequest;
import com.game.lottery.enums.VerificationLevel;
import com.game.lottery.model.User;
import com.game.lottery.model.UserOnboarding;
import com.game.lottery.model.UserProfile;
import com.game.lottery.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserProvisioningLogicTest {

    private UserProvisioningService userProvisioningService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private org.springframework.context.ApplicationEventPublisher applicationEventPublisher;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        System.out.println("DEBUG: userRepository is " + userRepository);
        userProvisioningService = new UserProvisioningService(userRepository, applicationEventPublisher);
        System.out.println("DEBUG: userProvisioningService created: " + userProvisioningService);
    }

    @Test
    void updateProfile_shouldPromoteToBasic_whenConditionsMet() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);

        UserProfile profile = new UserProfile();
        profile.setVerificationLevel(VerificationLevel.NEW);
        // Setting valid field to simulate existing email (e.g. from Google)
        profile.setEmail("test@test.com");

        UserOnboarding onboarding = new UserOnboarding();
        onboarding.setEmailVerified(true);
        onboarding.setEmailPresent(true);

        user.setProfile(profile);
        user.setChecklist(onboarding);
        profile.setUser(user);
        onboarding.setUser(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserUpdateRequest request = new UserUpdateRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setCountry("US");
        // We need fields that satisfy UserOnboarding.syncFromProfile
        // syncFromProfile checks: email, phone, firstName, lastName, country

        // Act
        userProvisioningService.updateProfile(userId, request);

        // Assert
        assertEquals("John", profile.getFirstName());
        assertEquals("Doe", profile.getLastName());
        assertEquals(VerificationLevel.BASIC, profile.getVerificationLevel());
    }

    @Test
    void ensureUserExists_shouldReturnExistingUser_whenFound() {
        // Arrange
        String sub = "keycloak-sub-123";

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject(sub)
                .claim("email", "test@test.com") // Add necessary claims if any
                .build();

        User existingUser = new User();
        existingUser.setId(UUID.randomUUID());
        UserProfile profile = UserProfile.empty(existingUser);
        profile.setEmail("test@test.com"); // Match the token email to avoid update logic triggering more NPEs if any
        existingUser.setProfile(profile);
        existingUser.setChecklist(UserOnboarding.empty(existingUser));
        when(userRepository.findByKeycloakSub(sub)).thenReturn(Optional.of(existingUser));

        // Act
        UUID resultId = userProvisioningService.ensureUserExists(jwt);

        // Assert
        assertEquals(existingUser.getId(), resultId);
        Mockito.verify(userRepository, Mockito.never())
                .save(ArgumentMatchers.any());
    }

    @Test
    void ensureUserExists_shouldCreateNewUser_whenNotFound() {
        // Arrange
        String sub = UUID.randomUUID().toString();

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject(sub)
                .claim("email", "new@test.com")
                .claim("given_name", "New")
                .claim("family_name", "User")
                .claim("email_verified", true)
                .build();

        when(userRepository.findByKeycloakSub(sub)).thenReturn(Optional.empty());

        // Act
        userProvisioningService.ensureUserExists(jwt);

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(sub, savedUser.getKeycloakSub());
        assertEquals("new@test.com", savedUser.getProfile().getEmail());
    }
}
