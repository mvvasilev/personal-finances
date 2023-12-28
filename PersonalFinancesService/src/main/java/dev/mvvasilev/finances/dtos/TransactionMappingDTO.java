package dev.mvvasilev.finances.dtos;

import dev.mvvasilev.finances.enums.MappingConversionType;
import dev.mvvasilev.finances.enums.ProcessedTransactionField;

public record TransactionMappingDTO(
        Long id,
        Long rawTransactionValueGroupId,
        ProcessedTransactionFieldDTO processedTransactionField,
        SupportedMappingConversionDTO conversionType,
        String trueBranchStringValue,
        String falseBranchStringValue
) {
}
