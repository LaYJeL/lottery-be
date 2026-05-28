-- liquibase formatted sql
-- changeset antigravity:006-backfill-wallets

INSERT INTO lottery.wallets (id, user_id, balance, currency, created_at, modified_at, created_by, modified_by)
SELECT
    gen_random_uuid(),
    u.id,
    0,
    'USD',
    NOW(),
    NOW(),
    u.id,
    u.id
FROM lottery.users u
WHERE NOT EXISTS (SELECT 1 FROM lottery.wallets w WHERE w.user_id = u.id);
