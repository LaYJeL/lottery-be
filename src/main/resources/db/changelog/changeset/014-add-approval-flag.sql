--liquibase formatted sql

--changeset winkratice:add-requires-approval-to-competitions
ALTER TABLE lottery.competitions ADD COLUMN requires_approval BOOLEAN DEFAULT FALSE NOT NULL;
