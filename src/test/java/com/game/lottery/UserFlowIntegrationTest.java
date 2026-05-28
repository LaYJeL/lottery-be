package com.game.lottery;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.lottery.dto.PhoneVerificationRequest;
import com.game.lottery.dto.UserUpdateRequest;
import com.game.lottery.enums.AccountStatus;
import com.game.lottery.enums.VerificationLevel;
import com.game.lottery.model.User;
import com.game.lottery.model.UserProfile;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // Disable security filters to mock CurrentUser manually
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

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .keycloakSub("test-sub-" + userId)
                .accountStatus(AccountStatus.ACTIVE)
                .authProvider(com.game.lottery.enums.AuthenticationProvider.LOCAL)
                .build();

        UserProfile profile = UserProfile.empty(user);
        profile.setEmail("flowtest@example.com");
        user.setProfile(profile);
        user.setChecklist(com.game.lottery.model.UserOnboarding.empty(user));

        userRepository.save(user);
    }

    @Test
    void fullUserFlow_shouldPromoteToBasic() throws Exception {
        try (MockedStatic<CurrentUser> currentUserMock = Mockito.mockStatic(CurrentUser.class)) {
            currentUserMock.when(CurrentUser::get).thenReturn(userId);

            mockMvc.perform(get("/api/v1/users/me"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.verificationLevel").value("NEW"));

            // 2. Fill Profile
            UserUpdateRequest updateRequest = new UserUpdateRequest();
            updateRequest.setFirstName("Alice");
            updateRequest.setLastName("Wonderland");
            updateRequest.setCountry("UK");
            updateRequest.setPhoneNumber("+441234567890");
            updateRequest.setBirthDate(LocalDate.of(1990, 1, 1));

            mockMvc.perform(put("/api/v1/users/me")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk());

            // 3. Request OTP
            mockMvc.perform(post("/api/v1/users/me/phone/otp"))
                    .andExpect(status().isOk());

            // 4. Verification: Get code from DB (since we can't read console logs in test
            // easily)
            String code = verificationCodeRepository.findAll().stream()
                    .filter(c -> c.getUserId().equals(userId))
                    .findFirst()
                    .orElseThrow()
                    .getCode();

            // 5. Submit OTP
            PhoneVerificationRequest verifyRequest = new PhoneVerificationRequest(code);
            mockMvc.perform(post("/api/v1/users/me/phone/verify")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(verifyRequest)))
                    .andExpect(status().isOk());

            // 6. Verify Promotion: Should be BASIC now
            // (Profile Filled + Phone Verified = BASIC)
            mockMvc.perform(get("/api/v1/users/me"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.firstName").value("Alice"))
                    .andExpect(jsonPath("$.phoneNumber").value("+441234567890"))
                    .andExpect(jsonPath("$.verificationLevel").value("BASIC"));
        }
    }
}
