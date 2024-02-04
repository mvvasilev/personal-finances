package dev.mvvasilev.finances.dtos;

import dev.mvvasilev.common.enums.RawTransactionValueType;
import dev.mvvasilev.finances.enums.CategorizationRule;

public record CategorizationRuleDTO(
        CategorizationRule rule,
        RawTransactionValueType applicableType
) {}
