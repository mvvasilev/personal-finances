package dev.mvvasilev.finances.dtos;

import dev.mvvasilev.common.enums.ProcessedTransactionField;
import dev.mvvasilev.finances.enums.CategorizationRule;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.Optional;

public record CreateCategorizationDTO (
        @NotNull
        CategorizationRule rule,

        @NotNull
        ProcessedTransactionField ruleBasedOn,

        @Length(max = 1024)
        Optional<String> stringValue,

        Optional<Double> numericGreaterThan,

        Optional<Double> numericLessThan,

        Optional<Double> numericValue,

        Optional<LocalDateTime> timestampGreaterThan,

        Optional<LocalDateTime> timestampLessThan,

        Optional<Boolean> booleanValue,

        @Valid
        CreateCategorizationDTO left,

        @Valid
        CreateCategorizationDTO right
) {
}
