-- V2__double_entry_ledger_and_idempotency.sql: Double-Entry Ledger and Idempotency Infrastructure

-- 1. Idempotency Records Table
CREATE TABLE IF NOT EXISTS idempotency_records (
    idempotency_key VARCHAR(128) PRIMARY KEY,
    user_email VARCHAR(255) NOT NULL,
    endpoint VARCHAR(128) NOT NULL,
    request_hash VARCHAR(64) NOT NULL,
    status VARCHAR(32) NOT NULL,
    response_body TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    expires_at TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_idemp_user_key ON idempotency_records(idempotency_key, user_email);

-- 2. Ledger Accounts Table
CREATE TABLE IF NOT EXISTS ledger_accounts (
    id BIGSERIAL PRIMARY KEY,
    account_code VARCHAR(64) NOT NULL UNIQUE,
    account_name VARCHAR(128) NOT NULL,
    account_type VARCHAR(32) NOT NULL,
    wallet_id BIGINT UNIQUE REFERENCES wallets(id) ON DELETE SET NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

-- 3. Journal Entries Table
CREATE TABLE IF NOT EXISTS journal_entries (
    id BIGSERIAL PRIMARY KEY,
    entry_number VARCHAR(64) NOT NULL UNIQUE,
    reference_id VARCHAR(64),
    type VARCHAR(32) NOT NULL,
    description VARCHAR(255),
    entry_date TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_je_ref_id ON journal_entries(reference_id);
CREATE INDEX IF NOT EXISTS idx_je_entry_date ON journal_entries(entry_date);

-- 4. Ledger Entries Table (Balanced Postings)
CREATE TABLE IF NOT EXISTS ledger_entries (
    id BIGSERIAL PRIMARY KEY,
    journal_entry_id BIGINT NOT NULL REFERENCES journal_entries(id) ON DELETE CASCADE,
    account_id BIGINT NOT NULL REFERENCES ledger_accounts(id) ON DELETE RESTRICT,
    entry_type VARCHAR(16) NOT NULL,
    amount NUMERIC(19, 4) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_le_account ON ledger_entries(account_id);
CREATE INDEX IF NOT EXISTS idx_le_journal ON ledger_entries(journal_entry_id);

-- Insert Default Platform Cash Settlement Account
INSERT INTO ledger_accounts (account_code, account_name, account_type, created_at)
VALUES ('ACC_PLATFORM_SETTLEMENT', 'Platform Cash Settlement Account', 'ASSET', NOW())
ON CONFLICT (account_code) DO NOTHING;
