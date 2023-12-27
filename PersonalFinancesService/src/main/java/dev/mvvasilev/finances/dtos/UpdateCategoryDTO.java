package dev.mvvasilev.finances.dtos;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record UpdateCategoryDTO (
        @NotNull
        @Length(max = 255)
        String name
) {
}
