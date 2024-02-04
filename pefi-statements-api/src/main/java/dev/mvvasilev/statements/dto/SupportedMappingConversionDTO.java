package dev.mvvasilev.statements.dto;


import dev.mvvasilev.common.enums.RawTransactionValueType;
import dev.mvvasilev.statements.enums.MappingConversionType;

public record SupportedMappingConversionDTO(
        MappingConversionType type,
        RawTransactionValueType from,
        RawTransactionValueType to
) {
}
