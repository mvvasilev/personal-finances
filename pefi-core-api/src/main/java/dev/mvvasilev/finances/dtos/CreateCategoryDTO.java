package dev.mvvasilev.finances.dtos;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record CreateCategoryDTO (
        @Length(max = 255)
        @NotNull
        String name
) {
}
