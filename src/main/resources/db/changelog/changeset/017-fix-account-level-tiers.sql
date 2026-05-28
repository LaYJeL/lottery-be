-- Fix account_level based on current_level for existing users
-- Tier thresholds: 1-9=NOVICE, 10-19=APPRENTICE, 20-29=BRONZE, 30-39=SILVER,
-- 40-49=GOLD, 50-59=PLATINUM, 60-69=EMERALD, 70-79=RUBY, 80-89=DIAMOND, 90+=LEGEND

UPDATE lottery.user_profiles
SET account_level = CASE
    WHEN current_level < 10 THEN 'NOVICE'
    WHEN current_level < 20 THEN 'APPRENTICE'
    WHEN current_level < 30 THEN 'BRONZE'
    WHEN current_level < 40 THEN 'SILVER'
    WHEN current_level < 50 THEN 'GOLD'
    WHEN current_level < 60 THEN 'PLATINUM'
    WHEN current_level < 70 THEN 'EMERALD'
    WHEN current_level < 80 THEN 'RUBY'
    WHEN current_level < 90 THEN 'DIAMOND'
    ELSE 'LEGEND'
END
WHERE account_level != CASE
    WHEN current_level < 10 THEN 'NOVICE'
    WHEN current_level < 20 THEN 'APPRENTICE'
    WHEN current_level < 30 THEN 'BRONZE'
    WHEN current_level < 40 THEN 'SILVER'
    WHEN current_level < 50 THEN 'GOLD'
    WHEN current_level < 60 THEN 'PLATINUM'
    WHEN current_level < 70 THEN 'EMERALD'
    WHEN current_level < 80 THEN 'RUBY'
    WHEN current_level < 90 THEN 'DIAMOND'
    ELSE 'LEGEND'
END;
