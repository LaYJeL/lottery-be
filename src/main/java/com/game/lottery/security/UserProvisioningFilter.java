package com.game.lottery.security;

import com.game.lottery.service.UserProvisioningService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@lombok.extern.slf4j.Slf4j
@RequiredArgsConstructor
public class UserProvisioningFilter extends OncePerRequestFilter {

    private final UserProvisioningService userProvisioningService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.info("UserProvisioningFilter entered for request: {}", request.getRequestURI());

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            log.info("Current authentication: {}", auth);

            if (auth instanceof JwtAuthenticationToken jwtAuth) {
                log.info("JWT detected. Ensuring user exists for subject: {}", jwtAuth.getToken().getSubject());
                UUID userId = userProvisioningService.ensureUserExists(jwtAuth.getToken());
                log.info("User provisioned/found: {}", userId);

                CurrentUser.set(userId);
            } else {
                log.info("No JwtAuthenticationToken found. Auth type: {}",
                        auth != null ? auth.getClass().getName() : "null");
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Error in UserProvisioningFilter: ", e);
            throw e;
        } finally {
            CurrentUser.clear();
        }
    }
}