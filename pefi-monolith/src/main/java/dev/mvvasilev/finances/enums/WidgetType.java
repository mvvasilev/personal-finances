package dev.mvvasilev.finances.enums;

import dev.mvvasilev.common.data.AbstractEnumConverter;
import dev.mvvasilev.common.data.PersistableEnum;

public enum WidgetType implements PersistableEnum<String> {
    TOTAL_SPENDING_PER_CATEGORY,
    SPENDING_OVER_TIME_PER_CATEGORY,
    SUM_PER_CATEGORY;

    @Override
    public String value() {
        return name();
    }

    public static class JpaConverter extends AbstractEnumConverter<WidgetType, String> {
        public JpaConverter() {
            super(WidgetType.class);
        }
    }
}
