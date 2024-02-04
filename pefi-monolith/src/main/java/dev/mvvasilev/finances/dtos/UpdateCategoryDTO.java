package dev.mvvasilev.finances.dtos;

import dev.mvvasilev.finances.enums.CategorizationRuleBehavior;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record UpdateCategoryDTO (
        @NotNull
        @Length(max = 255)
        String name,
        @NotNull
        CategorizationRuleBehavior ruleBehavior
) {
}
