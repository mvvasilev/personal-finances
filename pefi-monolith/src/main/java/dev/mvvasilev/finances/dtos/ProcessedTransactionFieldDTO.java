package dev.mvvasilev.finances.dtos;

import dev.mvvasilev.finances.enums.ProcessedTransactionField;
import dev.mvvasilev.finances.enums.RawTransactionValueType;

public record ProcessedTransactionFieldDTO(
        ProcessedTransactionField field,
        RawTransactionValueType type
) {
}
