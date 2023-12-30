package dev.mvvasilev.finances.enums;

import dev.mvvasilev.common.data.AbstractEnumConverter;
import dev.mvvasilev.common.data.PersistableEnum;

public enum CategorizationRuleBehavior implements PersistableEnum<String> {
    ANY,
    ALL,
    NONE;

    @Override
    public String value() {
        return name();
    }

    public static class JpaConverter extends AbstractEnumConverter<CategorizationRuleBehavior, String> {
        public JpaConverter() {
            super(CategorizationRuleBehavior.class);
        }
    }
}
