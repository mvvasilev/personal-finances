package dev.mvvasilev.finances.dtos;

import java.time.LocalDateTime;

public record WidgetParameterDTO (
        Long id,
        String name,
        String stringValue,
        Double numericValue,
        LocalDateTime timestampValue,
        Boolean booleanValue
) {}
