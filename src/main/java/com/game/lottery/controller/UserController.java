package com.game.lottery.controller;

import com.game.lottery.dto.UserEmailUpdateRequest;
import com.game.lottery.dto.UserOnboardingDto;
import com.game.lottery.dto.UserProfileDto;
import com.game.lottery.dto.UserUpdateRequest;
import com.game.lottery.model.User;
import com.game.lottery.model.UserProfile;
import com.game.lottery.repository.UserRepository;
import com.game.lottery.security.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.context.ApplicationEventPublisher;
import com.game.lottery.event.TaskActionEvent;
import com.game.lottery.enums.TaskActionType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserRepository userRepository;
    private final com.game.lottery.service.UserProvisioningService userProvisioningService;
    private final com.game.lottery.service.UserManagementService userManagementService;
    private final ApplicationEventPublisher eventPublisher;

    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getMe() {
        UUID currentUserId = CurrentUser.get();

        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalStateException("Current user not found in database"));

        // Publish Login Event (Async)
        eventPublisher.publishEvent(new TaskActionEvent(this, currentUserId, TaskActionType.LOGIN, 1));

        UserProfile profile = user.getProfile();

        UserProfileDto response = UserProfileDto.builder()
                .id(user.getId())
                .accountStatus(user.getAccountStatus())
                .email(profile.getEmail())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .middleName(profile.getMiddleName())
                .displayName(profile.getDisplayName())
                .phoneNumber(profile.getPhone())
                .country(profile.getCountry())
                .birthDate(profile.getBirthDate())
                .verificationLevel(profile.getVerificationLevel())
                // Stats & Gamification
                .reputation(profile.getReputation())
                .balance(profile.getBalance())
                .ticketsPurchased(profile.getTicketsPurchased())
                .competitionEntries(profile.getCompetitionEntries())
                .tasksCompleted(profile.getTasksCompleted())
                .totalWinnings(profile.getTotalWinnings())
                .accountLevel(profile.getAccountLevel())
                .currentLevel(profile.getCurrentLevel())
                .levelProgress(profile.getLevelProgress())
                // Auditing
                .createdAt(user.getCreatedAt())
                .modifiedAt(user.getModifiedAt())
                // Preferences
                .emailNotifications(profile.getEmailNotifications())
                .onboardingStatus(UserOnboardingDto.builder()
                        .emailVerified(user.getChecklist().isEmailVerified())
                        .phoneVerified(user.getChecklist().isPhoneVerified())
                        .emailPresent(user.getChecklist().isEmailPresent())
                        .phonePresent(user.getChecklist().isPhonePresent())
                        .firstNamePresent(user.getChecklist().isFirstNamePresent())
                        .lastNamePresent(user.getChecklist().isLastNamePresent())
                        .countryPresent(user.getChecklist().isCountryPresent())
                        .paymentMethodPresent(user.getChecklist().isPaymentMethodPresent())
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    public ResponseEntity<Void> updateProfile(
            @RequestBody UserUpdateRequest request) {
        UUID currentUserId = CurrentUser.get();
        userProvisioningService.updateProfile(currentUserId, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/me/email")
    public ResponseEntity<Void> updateEmail(
            @RequestBody @Valid UserEmailUpdateRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        UUID currentUserId = CurrentUser.get();
        String sub = jwt.getSubject();

        // 1. Update Keycloak
        userManagementService.updateEmail(sub, request.getEmail());

        // 2. Update Local DB (for immediate consistency)
        userProvisioningService.updateEmailLocal(currentUserId, request.getEmail());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteAccount(
            @AuthenticationPrincipal Jwt jwt) {

        UUID currentUserId = CurrentUser.get();
        String sub = jwt.getSubject();

        log.info("Request to deactivate account for user {}", currentUserId);

        // 1. Disable in Keycloak (prevent future logins)
        userManagementService.deactivateUser(sub);

        // 2. Mark as DELETED in local DB
        userProvisioningService.deactivateUser(currentUserId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/me/verification/document")
    public ResponseEntity<Void> submitVerificationDocument() {
        // Placeholder for passport/photo upload
        // Logic:
        // 1. Receives file
        // 2. Stores file (S3/MinIO)
        // 3. Creates VerificationRequest entity
        // 4. Admin reviews -> Promotes to VERIFIED
        return ResponseEntity.accepted().build();
    }
}
