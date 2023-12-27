package dev.mvvasilev.finances.dtos;

import dev.mvvasilev.finances.enums.CategorizationRule;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

public record CreateCategorizationDTO (
        @NotNull
        CategorizationRule rule,

        @Length(max = 1024)
        String stringValue,

        Double numericGreaterThan,

        Double numericLessThan,

        Double numericValue,

        LocalDateTime timestampGreaterThan,

        LocalDateTime timestampLessThan,

        Boolean booleanValue,

        @Valid
        CreateCategorizationDTO left,

        @Valid
        CreateCategorizationDTO right
) {
}
