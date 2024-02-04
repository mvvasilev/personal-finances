CREATE TABLE IF NOT EXISTS categories.processed_transaction_category (
    id BIGSERIAL,
    processed_transaction_id BIGINT,
    category_id BIGINT,
    CONSTRAINT PK_processed_transaction_category PRIMARY KEY (id),
    CONSTRAINT FK_processed_transaction_category_category FOREIGN KEY (category_id) REFERENCES categories.transaction_category (id) ON DELETE CASCADE,
    CONSTRAINT FK_processed_transaction_category_processed_transaction FOREIGN KEY (processed_transaction_id) REFERENCES transactions.processed_transaction(id) ON DELETE CASCADE
);