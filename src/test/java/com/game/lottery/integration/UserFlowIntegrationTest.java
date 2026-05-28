package com.game.lottery.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.lottery.dto.PhoneVerificationDto;
import com.game.lottery.dto.UserUpdateRequest;
import com.game.lottery.enums.VerificationLevel;
import com.game.lottery.model.User;
import com.game.lottery.repository.UserRepository;
import com.game.lottery.repository.VerificationCodeRepository;
import com.game.lottery.security.CurrentUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // Bypass security for this test flow (we mock CurrentUser/SecurityContext)
@ActiveProfiles("test")
@Transactional
public class UserFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // We need to bypass the JWT extraction in Controller/Service or mock
    // CurrentUser.
    // Since controllers use CurrentUser.get(), we can mock that static method.
    // However, ensureUserExists uses JWT.
    // For integration test, we can manually create the user first, then simulate
    // the flow.

    @BeforeEach
    void cleanUp() {
        verificationCodeRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void fullUserFlow_fromRegistrationToBasicVerification() throws Exception {
        UUID userId = UUID.randomUUID();

        // 1. Simulate "Registration" (User created via JWT login effect)
        // We'll manually insert the user as if they just logged in
        createInitialUser(userId);

        try (MockedStatic<CurrentUser> currentUserMock = Mockito.mockStatic(CurrentUser.class)) {
            currentUserMock.when(CurrentUser::get).thenReturn(userId);

            // 2. Initial state check: Should be NEW
            User user = userRepository.findById(userId).orElseThrow();
            assertThat(user.getProfile().getVerificationLevel()).isEqualTo(VerificationLevel.NEW);
            assertThat(user.getChecklist().isPhoneVerified()).isFalse();

            // 3. Update Profile (Add Phone Number & other fields)
            UserUpdateRequest updateRequest = UserUpdateRequest.builder()
                    .firstName("Jane")
                    .lastName("Doe")
                    .middleName("Marie")
                    .phoneNumber("+1234567890")
                    .country("US")
                    .birthDate(LocalDate.of(1990, 1, 1))
                    .build();

            mockMvc.perform(put("/api/v1/users/me")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk());

            // Verify update
            user = userRepository.findById(userId).orElseThrow();
            assertThat(user.getProfile().getPhone()).isEqualTo("+1234567890");
            assertThat(user.getProfile().getVerificationLevel()).isEqualTo(VerificationLevel.NEW); // Still NEW because
                                                                                                   // phone not verified

            // 4. Request OTP
            mockMvc.perform(post("/api/v1/users/me/phone/otp"))
                    .andExpect(status().isOk());

            // 5. Retrieve OTP from DB (Simulating user checking SMS)
            String code = verificationCodeRepository.findAll().get(0).getCode();

            // 6. Verify OTP
            PhoneVerificationDto verifyRequest = new PhoneVerificationDto(code);
            mockMvc.perform(post("/api/v1/users/me/phone/verify")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(verifyRequest)))
                    .andExpect(status().isOk());

            // 7. Verify Final State: Should be BASIC
            user = userRepository.findById(userId).orElseThrow();
            assertThat(user.getChecklist().isPhoneVerified()).isTrue();
            assertThat(user.getProfile().getVerificationLevel()).isEqualTo(VerificationLevel.BASIC);
        }
    }

    private void createInitialUser(UUID userId) {
        // Use UserProvisioningService style logic or manual builder
        // We do manual to save time and rely on DB state
        User user = User.builder()
                .id(userId)
                .keycloakSub("test-sub-" + userId)
                .accountStatus(com.game.lottery.enums.AccountStatus.ACTIVE)
                .authProvider(com.game.lottery.enums.AuthenticationProvider.LOCAL)
                .build();

        com.game.lottery.model.UserProfile profile = com.game.lottery.model.UserProfile.empty(user);
        profile.setEmail("jane@example.com");

        com.game.lottery.model.UserOnboarding checklist = com.game.lottery.model.UserOnboarding.empty(user);
        checklist.setEmailVerified(false); // Ensure email is NOT verified so we can test phone verification promotion

        user.setProfile(profile);
        user.setChecklist(checklist);

        userRepository.save(user);
    }
}
