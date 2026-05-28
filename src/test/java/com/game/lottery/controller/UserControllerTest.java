package com.game.lottery.controller;

import com.game.lottery.enums.AccountStatus;
import com.game.lottery.model.User;
import com.game.lottery.model.UserProfile;
import com.game.lottery.model.UserOnboarding;
import com.game.lottery.repository.UserRepository;
import com.game.lottery.security.CurrentUser;
import com.game.lottery.service.UserProvisioningService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false) // Bypass Security filters for this unit test
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserProvisioningService userProvisioningService;

    @MockBean
    private com.game.lottery.service.UserManagementService userManagementService; // Need explicit import or FQN if not
                                                                                  // imported

    private MockedStatic<CurrentUser> currentUserMock;

    @BeforeEach
    void setUp() {
        currentUserMock = Mockito.mockStatic(CurrentUser.class);
    }

    @AfterEach
    void tearDown() {
        currentUserMock.close();
    }

    @Test
    @WithMockUser
    void getMe_shouldReturnUserProfile() throws Exception {
        UUID userId = UUID.randomUUID();
        currentUserMock.when(CurrentUser::get).thenReturn(userId);

        User user = User.builder()
                .id(userId)
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        // Profile is created in PrePersist, but here we build manually or mock
        UserProfile profile = UserProfile.empty(user);
        profile.setEmail("test@example.com");
        profile.setFirstName("John");
        profile.setLastName("Doe");
        profile.setDisplayName("John Doe");
        profile.setDisplayName("John Doe");
        user.setProfile(profile);

        // Fix NPE: Initialize checklist as it is accessed in Controller
        UserOnboarding checklist = UserOnboarding.empty(user);
        user.setChecklist(checklist);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        mockMvc.perform(get("/api/v1/users/me")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"));
    }
}
