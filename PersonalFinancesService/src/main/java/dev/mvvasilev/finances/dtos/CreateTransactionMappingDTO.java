package dev.mvvasilev.finances.dtos;

import dev.mvvasilev.finances.enums.ProcessedTransactionField;
import jakarta.validation.constraints.NotNull;

public record CreateTransactionMappingDTO(
        @NotNull
        Long rawTransactionValueGroupId,
        @NotNull
        ProcessedTransactionField field
) {
}
