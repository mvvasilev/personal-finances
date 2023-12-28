package dev.mvvasilev.finances.dtos;

import dev.mvvasilev.finances.enums.MappingConversionType;
import dev.mvvasilev.finances.enums.RawTransactionValueType;

public record SupportedMappingConversionDTO(
        MappingConversionType type,
        RawTransactionValueType from,
        RawTransactionValueType to
) {
}
