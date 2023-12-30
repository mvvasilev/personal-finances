package dev.mvvasilev.finances.enums;

import dev.mvvasilev.common.data.AbstractEnumConverter;
import dev.mvvasilev.common.data.PersistableEnum;

// TODO: Create custom converter for JPA
public enum CategorizationRule implements PersistableEnum<String> {
    STRING_REGEX(RawTransactionValueType.STRING),
    STRING_EQ(RawTransactionValueType.STRING),
    STRING_CONTAINS(RawTransactionValueType.STRING),
    STRING_IS_EMPTY(RawTransactionValueType.STRING),
    NUMERIC_GREATER_THAN(RawTransactionValueType.NUMERIC),
    NUMERIC_LESS_THAN(RawTransactionValueType.NUMERIC),
    NUMERIC_EQUALS(RawTransactionValueType.NUMERIC),
    NUMERIC_BETWEEN(RawTransactionValueType.NUMERIC),
    TIMESTAMP_GREATER_THAN(RawTransactionValueType.TIMESTAMP),
    TIMESTAMP_LESS_THAN(RawTransactionValueType.TIMESTAMP),
    TIMESTAMP_BETWEEN(RawTransactionValueType.TIMESTAMP),
    BOOLEAN_EQ(RawTransactionValueType.BOOLEAN),
    AND(null),
    OR(null),
    NOT(null);

    final private RawTransactionValueType applicableForType;

    CategorizationRule(RawTransactionValueType applicableForType) {
        this.applicableForType = applicableForType;
    }

    public String value() {
        return name();
    }

    public RawTransactionValueType applicableForType() {
        return applicableForType;
    }

    public static class JpaConverter extends AbstractEnumConverter<CategorizationRule, String> {
        public JpaConverter() {
            super(CategorizationRule.class);
        }
    }
}
