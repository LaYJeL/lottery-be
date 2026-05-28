package com.game.lottery.dto;

import com.game.lottery.enums.AccountStatus;
import com.game.lottery.enums.VerificationLevel;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class UserProfileDto {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String middleName;
    private String displayName;
    private String phoneNumber;
    private String country;
    private LocalDate birthDate;
    private String aboutMe;
    private AccountStatus accountStatus;
    private VerificationLevel verificationLevel;
    private UserOnboardingDto onboardingStatus;

    private Integer reputation;
    private BigDecimal balance;
    private Integer ticketsPurchased;
    private Integer competitionEntries;
    private Integer tasksCompleted;
    private BigDecimal totalWinnings;
    private String accountLevel;
    private Integer levelProgress;
    private Integer currentLevel;

    private Boolean emailNotifications;

    private java.time.Instant createdAt;
    private java.time.Instant modifiedAt;
}
