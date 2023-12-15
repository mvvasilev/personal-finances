CREATE SCHEMA IF NOT EXISTS transactions;

CREATE TABLE IF NOT EXISTS transactions.raw_statement (
     id BIGSERIAL,
     time_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
     time_last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
     CONSTRAINT PK_raw_statement PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS transactions.raw_transaction_value_group (
    id BIGSERIAL,
    statement_id BIGINT,
    name VARCHAR(255),
    type SMALLINT,
    time_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    time_last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT PK_raw_transaction_value_group PRIMARY KEY (id),
    CONSTRAINT FK_raw_transaction_value_group_raw_statement FOREIGN KEY (statement_id) REFERENCES transactions.raw_statement(id)
);

CREATE TABLE IF NOT EXISTS transactions.raw_transaction_value (
    id BIGSERIAL,
    group_id BIGINT,
    string_value VARCHAR(1024),
    timestamp_value TIMESTAMP,
    numeric_value FLOAT,
    time_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    time_last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT PK_raw_transaction_value PRIMARY KEY (id),
    CONSTRAINT FK_raw_transaction_value_raw_transaction_value_group FOREIGN KEY (group_id) REFERENCES transactions.raw_transaction_value_group(id)
)