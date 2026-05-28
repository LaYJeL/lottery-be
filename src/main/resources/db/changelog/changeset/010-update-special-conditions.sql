--liquibase formatted sql

--changeset antigravity:add-special-conditions
UPDATE lottery.gamification_tasks SET condition_value = 'TIME:02:00-05:00' WHERE title = 'Night Owl';
UPDATE lottery.gamification_tasks SET condition_value = 'ROLE:BETA' WHERE title = 'Beta Tester';
