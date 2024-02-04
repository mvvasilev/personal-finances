package dev.mvvasilev.finances.enums;

import dev.mvvasilev.common.data.AbstractEnumConverter;
import dev.mvvasilev.common.data.PersistableEnum;

// TODO: Only single type of conversion currently supported: from string to boolean
// TODO: Add more: *_TO_NUMERIC, *_TO_TIMESTAMP, *_TO_STRING
// TODO: Also, figure out a better way to do these?
public enum MappingConversionType implements PersistableEnum<String> {
    STRING_TO_BOOLEAN(RawTransactionValueType.STRING, RawTransactionValueType.BOOLEAN);

    // TODO: Add the rest
    // STRING_TO_TIMESTAMP(RawTransactionValueType.STRING, RawTransactionValueType.TIMESTAMP),
    // STRING_TO_NUMERIC(RawTransactionValueType.STRING, RawTransactionValueType.NUMERIC),
    // BOOLEAN_TO_STRING(RawTransactionValueType.BOOLEAN, RawTransactionValueType.STRING),
    // BOOLEAN_TO_TIMESTAMP(RawTransactionValueType.BOOLEAN, RawTransactionValueType.TIMESTAMP),
    // BOOLEAN_TO_NUMERIC(RawTransactionValueType.BOOLEAN, RawTransactionValueType.NUMERIC),
    // TIMESTAMP_TO_STRING(RawTransactionValueType.TIMESTAMP, RawTransactionValueType.STRING),
    // TIMESTAMP_TO_BOOLEAN(RawTransactionValueType.TIMESTAMP, RawTransactionValueType.BOOLEAN),
    // TIMESTAMP_TO_NUMERIC(RawTransactionValueType.TIMESTAMP, RawTransactionValueType.NUMERIC),
    // NUMERIC_TO_STRING(RawTransactionValueType.NUMERIC, RawTransactionValueType.STRING),
    // NUMERIC_TO_BOOLEAN(RawTransactionValueType.NUMERIC, RawTransactionValueType.BOOLEAN),
    // NUMERIC_TO_TIMESTAMP(RawTransactionValueType.NUMERIC, RawTransactionValueType.TIMESTAMP);

    private final RawTransactionValueType from;
    private final RawTransactionValueType to;

    MappingConversionType(RawTransactionValueType from, RawTransactionValueType to) {
        this.from = from;
        this.to = to;
    }

    public RawTransactionValueType getFrom() {
        return from;
    }

    public RawTransactionValueType getTo() {
        return to;
    }

    @Override
    public String value() {
        return name();
    }

    public static class JpaConverter extends AbstractEnumConverter<MappingConversionType, String> {
        public JpaConverter() {
            super(MappingConversionType.class);
        }
    }
}
