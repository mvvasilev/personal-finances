CREATE SCHEMA IF NOT EXISTS transactions;

CREATE TABLE IF NOT EXISTS transactions.processed_transaction
(
    id                 BIGSERIAL,
    description        VARCHAR(1024),
    amount             FLOAT,
    is_inflow          BOOLEAN,
    category_id        BIGINT,
    timestamp          TIMESTAMP,
    time_created       TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    time_last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    statement_id       BIGINT,
    CONSTRAINT PK_processed_transaction PRIMARY KEY (id)
);