package com.game.lottery.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "game.balance")
public class GameBalanceConfig {

    private double levelingExponent = 1.5;
    private int baseXp = 100;
    private int maxLevel = 99;
    private int verificationCodeExpiryMinutes = 5;
    private int maxVerificationAttempts = 3;

    private Map<Integer, String> tierThresholds = Map.of(
            10, "NOVICE",
            20, "APPRENTICE",
            30, "BRONZE",
            40, "SILVER",
            50, "GOLD",
            60, "PLATINUM",
            70, "EMERALD",
            80, "RUBY",
            90, "DIAMOND"
    );
}
