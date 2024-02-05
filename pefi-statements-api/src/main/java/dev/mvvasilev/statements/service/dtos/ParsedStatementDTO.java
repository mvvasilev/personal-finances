package dev.mvvasilev.statements.service.dtos;

import dev.mvvasilev.statements.entity.RawStatement;
import dev.mvvasilev.statements.entity.RawTransactionValue;
import dev.mvvasilev.statements.entity.RawTransactionValueGroup;

import java.util.List;

public record ParsedStatementDTO(
        RawStatement statement,
        List<RawTransactionValueGroup> groups,
        List<RawTransactionValue> values
)
{ }
