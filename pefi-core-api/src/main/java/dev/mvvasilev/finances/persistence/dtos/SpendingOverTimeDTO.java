package dev.mvvasilev.finances.persistence.dtos;

import java.time.LocalDateTime;

public record SpendingOverTimeDTO(
        Long categoryId,

        Double amountForPeriod,

        LocalDateTime periodBeginningTimestamp
) {}
