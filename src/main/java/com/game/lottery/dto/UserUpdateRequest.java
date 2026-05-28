package com.game.lottery.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    private String firstName;
    private String lastName;
    private String middleName;
    private String phoneNumber;
    private String country;
    private LocalDate birthDate;
    private String displayName;
    private Boolean emailNotifications;
}
