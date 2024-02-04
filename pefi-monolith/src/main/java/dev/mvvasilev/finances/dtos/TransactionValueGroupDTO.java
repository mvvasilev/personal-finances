package dev.mvvasilev.finances.dtos;

import dev.mvvasilev.finances.enums.RawTransactionValueType;

public record TransactionValueGroupDTO(
        Long id,
        String name,
        RawTransactionValueType type
) {}
