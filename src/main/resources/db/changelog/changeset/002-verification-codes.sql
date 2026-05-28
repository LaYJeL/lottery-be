-- liquibase formatted sql

-- changeset winkratice:create-verification_codes-table
CREATE TABLE IF NOT EXISTS lottery.verification_codes (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    code VARCHAR(16) NOT NULL,
    type VARCHAR(32) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    attempts INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES lottery.users (id)
);

CREATE INDEX IF NOT EXISTS idx_verification_codes_user_id ON lottery.verification_codes(user_id);
