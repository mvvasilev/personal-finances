ALTER TABLE transactions.processed_transaction ADD COLUMN IF NOT EXISTS user_id INTEGER;

ALTER TABLE categories.transaction_category ADD COLUMN IF NOT EXISTS user_id INTEGER;