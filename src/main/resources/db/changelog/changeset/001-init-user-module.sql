--liquibase formatted sql

--changeset yevhenii:001-1-tables

CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS lottery.users (
                                             id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                             keycloak_sub VARCHAR(64) NOT NULL,
                                             auth_provider VARCHAR(16) NOT NULL DEFAULT 'LOCAL',
                                             account_status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE',
                                             created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                             modified_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                             created_by UUID,
                                             modified_by UUID,
                                             CONSTRAINT uk_users_keycloak_sub UNIQUE (keycloak_sub),
                                             CONSTRAINT ck_users_auth_provider CHECK (auth_provider IN ('GOOGLE','LOCAL')),
                                             CONSTRAINT ck_users_account_status CHECK (account_status IN ('ACTIVE','BLOCKED'))
);

CREATE TABLE IF NOT EXISTS lottery.user_profiles (
                                                     user_id UUID PRIMARY KEY,
                                                     email VARCHAR(320),
                                                     phone VARCHAR(32),
                                                     first_name VARCHAR(80),
                                                     last_name VARCHAR(80),
                                                     middle_name VARCHAR(80),
                                                     country VARCHAR(20),
                                                     birth_date DATE,
                                                     display_name VARCHAR(120),
                                                     verification_level VARCHAR(16) NOT NULL DEFAULT 'NEW',
                                                     created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                                     modified_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                                     created_by UUID,
                                                     modified_by UUID,
                                                     CONSTRAINT fk_profiles_user FOREIGN KEY (user_id) REFERENCES lottery.users(id),
                                                     CONSTRAINT ck_profiles_verification_level CHECK (verification_level IN ('NEW','BASIC','VERIFIED'))
);

CREATE TABLE IF NOT EXISTS lottery.payment_methods (
                                                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                       user_id UUID NOT NULL,
                                                       method_type VARCHAR(32) NOT NULL,
                                                       label VARCHAR(100),
                                                       identifier VARCHAR(255) NOT NULL,
                                                       provider_token VARCHAR(512),
                                                       is_primary BOOLEAN NOT NULL DEFAULT FALSE,
                                                       created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                                       modified_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                                       created_by UUID,
                                                       modified_by UUID,
                                                       CONSTRAINT fk_payment_methods_user FOREIGN KEY (user_id) REFERENCES lottery.users(id),
                                                       CONSTRAINT ck_payment_method_type CHECK (method_type IN ('CRYPTO_USDT_TRC20', 'CRYPTO_USDT_ERC20', 'CREDIT_CARD', 'PAYPAL'))
);

CREATE TABLE IF NOT EXISTS lottery.user_onboardings (
                                                        user_id UUID PRIMARY KEY,
                                                        email_verified BOOLEAN NOT NULL DEFAULT FALSE,
                                                        phone_verified BOOLEAN NOT NULL DEFAULT FALSE,
                                                        email_present BOOLEAN NOT NULL DEFAULT FALSE,
                                                        phone_present BOOLEAN NOT NULL DEFAULT FALSE,
                                                        first_name_present BOOLEAN NOT NULL DEFAULT FALSE,
                                                        last_name_present BOOLEAN NOT NULL DEFAULT FALSE,
                                                        country_present BOOLEAN NOT NULL DEFAULT FALSE,
                                                        payment_method_present BOOLEAN NOT NULL DEFAULT FALSE,
                                                        created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                                        modified_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                                        created_by UUID,
                                                        modified_by UUID,
                                                        CONSTRAINT fk_onboardings_user FOREIGN KEY (user_id) REFERENCES lottery.users(id)
);

--changeset yevhenii:001-2-functions splitStatements:false

DO $$
    BEGIN
        IF NOT EXISTS (
            SELECT 1 FROM pg_constraint c
                              JOIN pg_class t ON t.oid = c.conrelid
            WHERE c.conname = 'fk_users_created_by' AND t.relname = 'users'
        ) THEN
            ALTER TABLE lottery.users
                ADD CONSTRAINT fk_users_created_by
                    FOREIGN KEY (created_by) REFERENCES lottery.users(id) ON DELETE SET NULL;
        END IF;
    END $$;

CREATE OR REPLACE FUNCTION lottery.audit_timestamps()
    RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'UPDATE') THEN
        IF (NEW.modified_at IS NOT DISTINCT FROM OLD.modified_at) THEN
            NEW.modified_at = now();
        END IF;
    ELSE
        NEW.modified_at = now();
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DO $$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'tr_users_audit_timestamps') THEN
            CREATE TRIGGER tr_users_audit_timestamps BEFORE INSERT OR UPDATE ON lottery.users FOR EACH ROW EXECUTE FUNCTION lottery.audit_timestamps();
        END IF;
        IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'tr_user_profiles_audit_timestamps') THEN
            CREATE TRIGGER tr_user_profiles_audit_timestamps BEFORE INSERT OR UPDATE ON lottery.user_profiles FOR EACH ROW EXECUTE FUNCTION lottery.audit_timestamps();
        END IF;
        IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'tr_user_onboardings_audit_timestamps') THEN
            CREATE TRIGGER tr_user_onboardings_audit_timestamps BEFORE INSERT OR UPDATE ON lottery.user_onboardings FOR EACH ROW EXECUTE FUNCTION lottery.audit_timestamps();
        END IF;
        IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'tr_payment_methods_audit_timestamps') THEN
            CREATE TRIGGER tr_payment_methods_audit_timestamps BEFORE INSERT OR UPDATE ON lottery.payment_methods FOR EACH ROW EXECUTE FUNCTION lottery.audit_timestamps();
        END IF;
    END $$;