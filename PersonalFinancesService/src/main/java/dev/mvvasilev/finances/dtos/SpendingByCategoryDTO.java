package dev.mvvasilev.finances.dtos;

import java.util.Collection;
import java.util.Map;

public record SpendingByCategoryDTO(
        Collection<CategoryDTO> categories,

        Map<Long, Double> spendingByCategory
) {
}
