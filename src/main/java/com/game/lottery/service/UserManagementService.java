package com.game.lottery.service;

import com.game.lottery.exception.IdentityProviderException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserManagementService {

    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    public void deactivateUser(String userSub) {
        log.info("Deactivating user {} in Keycloak", userSub);
        try {
            UserResource userResource = keycloak.realm(realm).users().get(userSub);
            UserRepresentation user = userResource.toRepresentation();
            user.setEnabled(false);
            userResource.update(user);
            log.info("User {} disabled in Keycloak", userSub);
        } catch (Exception e) {
            log.error("Failed to deactivate user {}", userSub, e);
            throw new IdentityProviderException("Failed to deactivate user in identity provider", e);
        }
    }

    public void updateEmail(String userSub, String newEmail) {
        log.info("Updating email for user {} to {} in Keycloak", userSub, newEmail);

        try {
            UserResource userResource = keycloak.realm(realm).users().get(userSub);
            UserRepresentation user = userResource.toRepresentation();

            // Check if email is actually different to avoid redundant updates
            if (newEmail.equals(user.getEmail())) {
                log.info("Email is already set to {}", newEmail);
                return;
            }

            user.setEmail(newEmail);
            user.setEmailVerified(false); // Force re-verification

            // If we are using email-as-username, update username too?
            // "registrationEmailAsUsername" only applies at registration.
            // But if we enforce it, we should likely update username too if it was the old
            // email.
            // Check if username == old email
            if (user.getUsername() != null && user.getUsername().equals(user.getEmail())) {
                // user.setUsername(newEmail); // Keycloak might block editing username if
                // editUsernameAllowed is false?
                // But we are ADMIN. We can do it.
                // Ideally, keep username in sync if used as login.
                user.setUsername(newEmail);
            }

            userResource.update(user);

            // Trigger Verify Email Action
            userResource.executeActionsEmail(Collections.singletonList("VERIFY_EMAIL"));

            log.info("Email updated and verification email sent.");
        } catch (Exception e) {
            log.error("Failed to update email for user {}", userSub, e);
            throw new IdentityProviderException("Failed to update email in identity provider", e);
        }
    }
}
