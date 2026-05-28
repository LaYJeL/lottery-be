package com.game.lottery.model;

import com.game.lottery.enums.VerificationLevel;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_profiles", schema = "lottery")
public class UserProfile extends Auditable {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "email", length = 320)
    private String email;

    @Column(name = "phone", length = 32)
    private String phone;

    @Column(name = "first_name", length = 80)
    private String firstName;

    @Column(name = "last_name", length = 80)
    private String lastName;

    @Column(name = "middle_name", length = 80)
    private String middleName;

    @Column(name = "country", length = 2)
    private String country;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "display_name", length = 120)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_level", nullable = false, length = 16)
    private VerificationLevel verificationLevel;

    @Column(name = "reputation", nullable = false)
    @Builder.Default
    private Integer reputation = 0;

    @Column(name = "balance", nullable = false)
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "tickets_purchased", nullable = false)
    @Builder.Default
    private Integer ticketsPurchased = 0;

    @Column(name = "competition_entries", nullable = false)
    @Builder.Default
    private Integer competitionEntries = 0;

    @Column(name = "tasks_completed", nullable = false)
    @Builder.Default
    private Integer tasksCompleted = 0;

    @Column(name = "total_winnings", nullable = false)
    @Builder.Default
    private BigDecimal totalWinnings = BigDecimal.ZERO;

    @Column(name = "account_level", nullable = false, length = 32)
    @Builder.Default
    private String accountLevel = "NOVICE";

    @Column(name = "level_progress", nullable = false)
    @Builder.Default
    private Integer levelProgress = 0;

    @Column(name = "current_level", nullable = false)
    @Builder.Default
    private Integer currentLevel = 1;

    @Column(name = "email_notifications", nullable = false)
    @Builder.Default
    private Boolean emailNotifications = true;

    public static UserProfile empty(User user) {
        return UserProfile.builder()
                .user(user)
                .verificationLevel(VerificationLevel.NEW)
                .build();
    }
}
