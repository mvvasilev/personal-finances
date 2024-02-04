package dev.mvvasilev.finances.dtos;

import dev.mvvasilev.finances.enums.CategorizationRuleBehavior;

public record CategoryDTO(
        Long id,
        String name,
        CategorizationRuleBehavior ruleBehavior
) {
}
