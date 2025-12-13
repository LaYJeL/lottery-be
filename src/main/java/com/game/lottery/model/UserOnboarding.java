package com.game.lottery.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_onboardings", schema = "lottery")
public class UserOnboarding extends Auditable {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified;

    @Column(name = "phone_verified", nullable = false)
    private boolean phoneVerified;

    @Column(name = "email_present", nullable = false)
    private boolean emailPresent;

    @Column(name = "phone_present", nullable = false)
    private boolean phonePresent;

    @Column(name = "first_name_present", nullable = false)
    private boolean firstNamePresent;

    @Column(name = "last_name_present", nullable = false)
    private boolean lastNamePresent;

    @Column(name = "country_present", nullable = false)
    private boolean countryPresent;

    @Column(name = "payment_method_present", nullable = false)
    private boolean paymentMethodPresent;

    public static UserOnboarding empty(User user) {
        return UserOnboarding.builder()
                .user(user)
                .emailVerified(false)
                .phoneVerified(false)
                .emailPresent(false)
                .phonePresent(false)
                .firstNamePresent(false)
                .lastNamePresent(false)
                .countryPresent(false)
                .paymentMethodPresent(false)
                .build();
    }

    public void syncFromProfile(UserProfile p) {
        this.emailPresent = p.getEmail() != null && !p.getEmail().isBlank();
        this.phonePresent = p.getPhone() != null && !p.getPhone().isBlank();
        this.firstNamePresent = p.getFirstName() != null && !p.getFirstName().isBlank();
        this.lastNamePresent = p.getLastName() != null && !p.getLastName().isBlank();
        this.countryPresent = p.getCountry() != null && !p.getCountry().isBlank();
    }

    public void updatePaymentStatus(boolean hasMethods) {
        this.paymentMethodPresent = hasMethods;
    }
}
