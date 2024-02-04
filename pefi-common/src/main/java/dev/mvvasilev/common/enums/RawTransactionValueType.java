package dev.mvvasilev.common.enums;

import dev.mvvasilev.common.data.PersistableEnum;

// TODO: Create custom converter for JPA
public enum RawTransactionValueType implements PersistableEnum<String> {
    STRING,
    NUMERIC,
    TIMESTAMP,
    BOOLEAN;

    @Override
    public String value() {
        return name();
    }
}
