package dev.mvvasilev.widgets.dtos;

import java.time.LocalDateTime;

public record CreateWidgetParameterDTO (
    String name,
    String stringValue,
    Double numericValue,
    LocalDateTime timestampValue,
    Boolean booleanValue
) {}
