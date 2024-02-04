package dev.mvvasilev.finances.dtos;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Collection;
import java.util.Map;

public record SpendingOverTimeByCategoryDTO(
        Collection<CategoryDTO> categories,

        Period timePeriodDuration,

        Collection<SpendingByCategoryDTO> spending
) {
}
