-- liquibase formatted sql

-- changeset antigravity:add-user-level-column
ALTER TABLE lottery.user_profiles
ADD COLUMN current_level INTEGER NOT NULL DEFAULT 1;
