package dev.mvvasilev.statements.dto;

import dev.mvvasilev.common.enums.ProcessedTransactionField;
import dev.mvvasilev.statements.enums.MappingConversionType;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record CreateTransactionMappingDTO(
        @NotNull
        Long rawTransactionValueGroupId,
        @NotNull
        ProcessedTransactionField field,
        MappingConversionType conversionType,
        @Length(max = 255)
        String trueBranchStringValue,
        @Length(max = 255)
        String falseBranchStringValue
) {
}
