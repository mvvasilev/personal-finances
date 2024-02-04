package dev.mvvasilev.finances.dtos;

import java.time.LocalDateTime;

public record SpendingByCategoryDTO (
    Long categoryId,

    LocalDateTime timestamp,

    Double amount
) {}
