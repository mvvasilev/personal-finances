package dev.mvvasilev.finances.dtos;

import dev.mvvasilev.finances.enums.CategorizationRule;
import dev.mvvasilev.finances.enums.RawTransactionValueType;

public record CategorizationRuleDTO(
        CategorizationRule rule,
        RawTransactionValueType applicableType
) {}
