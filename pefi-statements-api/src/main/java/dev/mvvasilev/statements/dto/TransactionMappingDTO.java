package dev.mvvasilev.statements.dto;

import dev.mvvasilev.common.dto.ProcessedTransactionFieldDTO;

public record TransactionMappingDTO(
        Long id,
        Long rawTransactionValueGroupId,
        ProcessedTransactionFieldDTO processedTransactionField,
        SupportedMappingConversionDTO conversionType,
        String trueBranchStringValue,
        String falseBranchStringValue
) {
}
