package dev.mvvasilev.finances.dtos;

import dev.mvvasilev.common.enums.ProcessedTransactionField;
import dev.mvvasilev.common.enums.RawTransactionValueType;

public record ProcessedTransactionFieldDTO(
        ProcessedTransactionField field,
        RawTransactionValueType type
) {
}
