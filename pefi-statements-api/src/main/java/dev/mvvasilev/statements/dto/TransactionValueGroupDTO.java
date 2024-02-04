package dev.mvvasilev.statements.dto;


import dev.mvvasilev.common.enums.RawTransactionValueType;

public record TransactionValueGroupDTO(
        Long id,
        String name,
        RawTransactionValueType type
) {}
