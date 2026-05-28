package com.game.lottery.security;

import com.game.lottery.config.SecurityConfig;
import com.game.lottery.service.UserProvisioningService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RoleSecurityTest.TestController.class)
@Import(SecurityConfig.class)
public class RoleSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserProvisioningService userProvisioningService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminEndpoint_shouldAllowAdmin() throws Exception {
        mockMvc.perform(get("/test/admin"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void adminEndpoint_shouldForbidUser() throws Exception {
        mockMvc.perform(get("/test/admin"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void userEndpoint_shouldAllowUser() throws Exception {
        mockMvc.perform(get("/test/user"))
                .andExpect(status().isOk());
    }

    @RestController
    static class TestController {
        @GetMapping("/test/admin")
        @PreAuthorize("hasRole('ADMIN')")
        public String admin() {
            return "Admin Content";
        }

        @GetMapping("/test/user")
        @PreAuthorize("hasRole('USER')")
        public String user() {
            return "User Content";
        }
    }
}
