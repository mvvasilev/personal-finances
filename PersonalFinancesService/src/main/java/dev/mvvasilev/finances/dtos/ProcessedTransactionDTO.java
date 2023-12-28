package dev.mvvasilev.finances.dtos;

import java.time.LocalDateTime;
import java.util.Collection;

public record ProcessedTransactionDTO(
        Long id,
        Double amount,
        boolean isInflow,
        LocalDateTime timestamp,
        String description,
        Collection<TransactionCategoryDTO> categories
) {}
