package dev.mvvasilev.finances.dtos;

import java.util.Collection;
import java.util.Map;

public record SpendingOverTimeByCategoryDTO(
        Collection<CategoryDTO> categories,

        Map<Long, Collection<Double>> spendingOverTime
) {
}
