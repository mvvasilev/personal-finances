CREATE SCHEMA IF NOT EXISTS widgets;

CREATE TABLE IF NOT EXISTS widgets.widget (
    id BIGSERIAL,
    type VARCHAR(255),
    positionX INTEGER,
    positionY INTEGER,
    sizeX INTEGER,
    sizeY INTEGER,
    name VARCHAR(255),
    user_id INTEGER,
    time_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    time_last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT PK_widget PRIMARY KEY (id)
)