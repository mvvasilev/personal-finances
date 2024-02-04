CREATE SCHEMA IF NOT EXISTS categories;

CREATE TABLE IF NOT EXISTS categories.transaction_category (
    id BIGSERIAL,
    name VARCHAR(255),
    time_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    time_last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT PK_transaction_category PRIMARY KEY (id)
)