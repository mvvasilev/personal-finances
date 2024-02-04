package dev.mvvasilev.finances.dtos;

import dev.mvvasilev.finances.enums.MappingConversionType;
import dev.mvvasilev.finances.enums.ProcessedTransactionField;
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
