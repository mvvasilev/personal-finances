CREATE TABLE IF NOT EXISTS widgets.widget_parameter (
    id BIGSERIAL,
    widget_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    string_value VARCHAR(1024),
    numeric_value FLOAT,
    timestamp_value TIMESTAMP,
    boolean_value BOOLEAN,
    time_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    time_last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT PK_widget_parameter PRIMARY KEY (id),
    CONSTRAINT FK_widget_parameter_widget FOREIGN KEY (widget_id) REFERENCES widgets.widget(id) ON DELETE CASCADE
);