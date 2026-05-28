-- liquibase formatted sql

-- changeset antigravity:add-gamification-tables
CREATE TABLE lottery.gamification_tasks (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(512),
    category VARCHAR(32) NOT NULL,
    action_type VARCHAR(32) NOT NULL,
    condition_value VARCHAR(255),
    target_count INTEGER NOT NULL DEFAULT 1,
    reward_points INTEGER NOT NULL DEFAULT 0,
    reward_currency NUMERIC(19, 2),
    required_level VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    modified_at TIMESTAMP WITHOUT TIME ZONE,
    created_by UUID,
    modified_by UUID
);

CREATE TABLE lottery.user_task_progress (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    task_id UUID NOT NULL,
    current_count INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(32) NOT NULL DEFAULT 'IN_PROGRESS',
    last_updated_at TIMESTAMP WITHOUT TIME ZONE,
    cycle_start_at TIMESTAMP WITHOUT TIME ZONE,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    modified_at TIMESTAMP WITHOUT TIME ZONE,
    created_by UUID,
    modified_by UUID,
    CONSTRAINT fk_user_task_progress_user FOREIGN KEY (user_id) REFERENCES lottery.users (id),
    CONSTRAINT fk_user_task_progress_task FOREIGN KEY (task_id) REFERENCES lottery.gamification_tasks (id),
    CONSTRAINT uk_user_task_progress UNIQUE (user_id, task_id)
);
