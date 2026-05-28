--liquibase formatted sql

--changeset antigravity:add-task-icon
ALTER TABLE lottery.gamification_tasks ADD COLUMN icon VARCHAR(255);

--changeset antigravity:backfill-task-icons
-- Backfill icons for existing tasks based on title/category (simplified)

-- Daily
UPDATE lottery.gamification_tasks SET icon = '🌞' WHERE category = 'DAILY' AND title = 'Early Riser';
UPDATE lottery.gamification_tasks SET icon = '🎟️' WHERE category = 'DAILY' AND title = 'Ticket Master';
UPDATE lottery.gamification_tasks SET icon = '🎲' WHERE category = 'DAILY' AND title = 'Daily Player';
UPDATE lottery.gamification_tasks SET icon = '📊' WHERE category = 'DAILY' AND title = 'Results Watcher';

-- Weekly
UPDATE lottery.gamification_tasks SET icon = '🔥' WHERE category = 'WEEKLY' AND title = 'Weekly Login Streak';
UPDATE lottery.gamification_tasks SET icon = '🎰' WHERE category = 'WEEKLY' AND title = 'High Roller';
UPDATE lottery.gamification_tasks SET icon = '🏆' WHERE category = 'WEEKLY' AND title = 'Competition Spirit';
UPDATE lottery.gamification_tasks SET icon = '💰' WHERE category = 'WEEKLY' AND title = 'Weekend Deposit';
UPDATE lottery.gamification_tasks SET icon = '🗳️' WHERE category = 'WEEKLY' AND title = 'Civic Duty';

-- One-Time
UPDATE lottery.gamification_tasks SET icon = '🆔' WHERE category = 'ONETIME' AND title = 'Verified Identity';
UPDATE lottery.gamification_tasks SET icon = '💳' WHERE category = 'ONETIME' AND title = 'First Deposit';
UPDATE lottery.gamification_tasks SET icon = '🎫' WHERE category = 'ONETIME' AND title = 'First Ticket';
UPDATE lottery.gamification_tasks SET icon = '🤝' WHERE category = 'ONETIME' AND title = 'Referral Starter';

-- Achievements
UPDATE lottery.gamification_tasks SET icon = '🦋' WHERE category = 'ACHIEVEMENT' AND title = 'Socialite';
UPDATE lottery.gamification_tasks SET icon = '🏰' WHERE category = 'ACHIEVEMENT' AND title = 'Loyal Member';
UPDATE lottery.gamification_tasks SET icon = '🐋' WHERE category = 'ACHIEVEMENT' AND title = 'Big Spender';
UPDATE lottery.gamification_tasks SET icon = '🗣️' WHERE category = 'ACHIEVEMENT' AND title = 'Opinion Leader';
UPDATE lottery.gamification_tasks SET icon = '🏦' WHERE category = 'ACHIEVEMENT' AND title = 'Deposit Hero';

-- Special
UPDATE lottery.gamification_tasks SET icon = '🎂' WHERE category = 'SPECIAL' AND title = 'Birthday Bonus';
UPDATE lottery.gamification_tasks SET icon = '🏹' WHERE category = 'SPECIAL' AND title = 'Promo Hunter';
