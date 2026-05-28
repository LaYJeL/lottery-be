--liquibase formatted sql

--changeset antigravity:add-birthday-condition
UPDATE lottery.gamification_tasks SET condition_value = 'BIRTHDAY' WHERE title = 'Birthday Bonus';
