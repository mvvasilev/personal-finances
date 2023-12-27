package dev.mvvasilev.finances.dtos;

import dev.mvvasilev.finances.enums.CategorizationRule;

import java.time.LocalDateTime;

public record CategorizationDTO(
        Long id,

        CategorizationRule rule,

        String stringValue,

        Double numericGreaterThan,

        Double numericLessThan,

        Double numericValue,

        LocalDateTime timestampGreaterThan,

        LocalDateTime timestampLessThan,

        Boolean booleanValue,

        CategorizationDTO left,

        CategorizationDTO right
) {
}
