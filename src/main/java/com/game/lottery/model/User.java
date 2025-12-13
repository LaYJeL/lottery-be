package com.game.lottery.model;

import com.game.lottery.enums.AccountStatus;
import com.game.lottery.enums.AuthenticationProvider;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "users",
        schema = "lottery",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_keycloak_sub", columnNames = "keycloak_sub")
        }
)
public class User extends Auditable {

    @Id
    private UUID id;

    @Column(name = "keycloak_sub", nullable = false, updatable = false, length = 64)
    private String keycloakSub;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider", nullable = false, length = 16)
    private AuthenticationProvider authProvider;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", nullable = false, length = 16)
    private AccountStatus accountStatus;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false, orphanRemoval = true)
    private UserProfile profile;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false, orphanRemoval = true)
    private UserOnboarding checklist;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentMethod> paymentMethods = new ArrayList<>();

    @PrePersist
    void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }

        if (accountStatus == null) {
            accountStatus = AccountStatus.ACTIVE;
        }

        if (profile == null) {
            profile = UserProfile.empty(this);
        } else {
            profile.setUser(this);
        }

        if (checklist == null) {
            checklist = UserOnboarding.empty(this);
        } else {
            checklist.setUser(this);
        }

        checklist.syncFromProfile(profile);

        if (getCreatedBy() == null) {
            setCreatedBy(id);
        }
        if (getModifiedBy() == null) {
            setModifiedBy(id);
        }
    }
}
