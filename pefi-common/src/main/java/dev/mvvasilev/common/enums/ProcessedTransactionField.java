package dev.mvvasilev.common.enums;

import dev.mvvasilev.common.data.AbstractEnumConverter;
import dev.mvvasilev.common.data.PersistableEnum;

public enum ProcessedTransactionField implements PersistableEnum<String> {
    DESCRIPTION(RawTransactionValueType.STRING),
    AMOUNT(RawTransactionValueType.NUMERIC),
    IS_INFLOW(RawTransactionValueType.BOOLEAN),
    TIMESTAMP(RawTransactionValueType.TIMESTAMP);

    final private RawTransactionValueType type;

    ProcessedTransactionField(RawTransactionValueType type) {
        this.type = type;
    }

    public String value() {
        return name();
    }

    public RawTransactionValueType type() {
        return type;
    }

    public static class JpaConverter extends AbstractEnumConverter<ProcessedTransactionField, String> {
        public JpaConverter() {
            super(ProcessedTransactionField.class);
        }
    }
}
