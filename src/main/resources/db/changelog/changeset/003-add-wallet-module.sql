-- liquibase formatted sql
-- changeset antigravity:005-add-wallet-module

CREATE TABLE IF NOT EXISTS lottery.wallets (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE REFERENCES lottery.users(id),
    balance DECIMAL(19, 2) NOT NULL DEFAULT 0,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    version BIGINT,
    created_at TIMESTAMP NOT NULL,
    modified_at TIMESTAMP NOT NULL,
    created_by UUID,
    modified_by UUID
);


CREATE TABLE IF NOT EXISTS lottery.transactions (
    id UUID PRIMARY KEY,
    wallet_id UUID NOT NULL REFERENCES lottery.wallets(id),
    payment_method_id UUID REFERENCES lottery.payment_methods(id),
    type VARCHAR(32) NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    status VARCHAR(32) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    modified_at TIMESTAMP NOT NULL,
    created_by UUID,
    modified_by UUID
);

CREATE INDEX IF NOT EXISTS idx_transactions_wallet_id ON lottery.transactions(wallet_id);
