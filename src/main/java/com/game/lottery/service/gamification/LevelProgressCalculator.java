package com.game.lottery.service.gamification;

import com.game.lottery.config.GameBalanceConfig;
import com.game.lottery.model.UserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class LevelProgressCalculator {

    private final GameBalanceConfig config;

    public void updateLevelProgress(UserProfile profile) {
        long totalReputation = profile.getReputation();
        int baseXp = config.getBaseXp();
        double exponent = config.getLevelingExponent();
        int maxLevel = config.getMaxLevel();

        if (totalReputation < baseXp) {
            profile.setCurrentLevel(1);
            profile.setLevelProgress((int) totalReputation);
        } else {
            int calculatedLevel = (int) Math.pow(totalReputation / (double) baseXp, 1.0 / exponent);
            calculatedLevel = Math.max(1, Math.min(maxLevel, calculatedLevel));

            profile.setCurrentLevel(calculatedLevel);

            int xpForCurrent = calculateXpForLevel(calculatedLevel);
            int xpForNext = calculateXpForLevel(calculatedLevel + 1);

            int xpNeeded = xpForNext - xpForCurrent;
            int xpInLevel = (int) totalReputation - xpForCurrent;

            if (xpNeeded > 0) {
                int progressPercent = (int) ((double) xpInLevel / xpNeeded * 100);
                profile.setLevelProgress(Math.min(100, progressPercent));
            } else {
                profile.setLevelProgress(100);
            }
        }

        String newRank = getRankForLevel(profile.getCurrentLevel());
        profile.setAccountLevel(newRank);
    }

    public int calculateXpForLevel(int level) {
        return (int) (config.getBaseXp() * Math.pow(level, config.getLevelingExponent()));
    }

    public String getRankForLevel(int level) {
        Map<Integer, String> thresholds = config.getTierThresholds();

        // Find the highest threshold that the level exceeds
        String rank = "LEGEND"; // Default for max level
        int highestMatchingThreshold = 0;

        for (Map.Entry<Integer, String> entry : thresholds.entrySet()) {
            if (level < entry.getKey() && entry.getKey() > highestMatchingThreshold) {
                // This is a simpler approach - find the first threshold the level is below
                if (level < 10) return thresholds.getOrDefault(10, "NOVICE");
                if (level < 20) return thresholds.getOrDefault(20, "APPRENTICE");
                if (level < 30) return thresholds.getOrDefault(30, "BRONZE");
                if (level < 40) return thresholds.getOrDefault(40, "SILVER");
                if (level < 50) return thresholds.getOrDefault(50, "GOLD");
                if (level < 60) return thresholds.getOrDefault(60, "PLATINUM");
                if (level < 70) return thresholds.getOrDefault(70, "EMERALD");
                if (level < 80) return thresholds.getOrDefault(80, "RUBY");
                if (level < 90) return thresholds.getOrDefault(90, "DIAMOND");
            }
        }

        return rank;
    }
}
