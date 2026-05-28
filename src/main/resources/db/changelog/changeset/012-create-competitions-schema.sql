--liquibase formatted sql

--changeset antigravity:create-competitions-schema
CREATE TABLE lottery.competitions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    description TEXT,
    prize VARCHAR(255),
    entry_fee DECIMAL(19, 2) NOT NULL DEFAULT 0,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    status VARCHAR(50) NOT NULL,
    image_url VARCHAR(500),
    participants_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    modified_by UUID
);

CREATE TABLE lottery.competition_entries (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    competition_id UUID NOT NULL REFERENCES lottery.competitions(id),
    user_id UUID NOT NULL REFERENCES lottery.user_profiles(user_id),
    content TEXT,
    status VARCHAR(50) NOT NULL,
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    votes INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    modified_by UUID
);

CREATE INDEX idx_competitions_status ON lottery.competitions(status);
CREATE INDEX idx_entries_competition_user ON lottery.competition_entries(competition_id, user_id);
