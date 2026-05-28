package com.game.lottery.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserOnboardingDto {
    private boolean emailVerified;
    private boolean phoneVerified;
    private boolean emailPresent;
    private boolean phonePresent;
    private boolean firstNamePresent;
    private boolean lastNamePresent;
    private boolean countryPresent;
    private boolean paymentMethodPresent;
}
