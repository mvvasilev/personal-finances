package dev.mvvasilev.finances.dtos;

import dev.mvvasilev.finances.enums.ProcessedTransactionField;

public record TransactionMappingDTO(
        Long id,
        Long rawTransactionValueGroupId,
        ProcessedTransactionField processedTransactionField
) {
}
