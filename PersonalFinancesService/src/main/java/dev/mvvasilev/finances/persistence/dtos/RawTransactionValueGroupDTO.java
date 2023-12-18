package dev.mvvasilev.finances.persistence.dtos;

import dev.mvvasilev.finances.enums.RawTransactionValueType;

public interface RawTransactionValueGroupDTO {

    Long getId();

    String getName();

    int getType();

}
