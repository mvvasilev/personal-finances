CREATE TABLE IF NOT EXISTS trasactions.transaction_mapping (
    id BIGSERIAL,
    time_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    time_last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    raw_transaction_value_group_id BIGINT,
    processed_transaction_field VARCHAR(255),
    CONSTRAINT PK_transaction_mapping PRIMARY KEY (id),
    CONSTRAINT FK_transaction_mapping_raw_transaction_value_group FOREIGN KEY(raw_transaction_value_group_id) REFERENCES transactions.raw_transaction_value_group(id) ON DELETE CASCADE
);