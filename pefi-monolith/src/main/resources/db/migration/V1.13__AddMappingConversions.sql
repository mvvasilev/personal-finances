ALTER TABLE transactions.transaction_mapping
    ADD COLUMN IF NOT EXISTS conversion_type VARCHAR(255),
    ADD COLUMN IF NOT EXISTS true_branch_string_value VARCHAR(255),
    ADD COLUMN IF NOT EXISTS false_branch_string_value VARCHAR(255);