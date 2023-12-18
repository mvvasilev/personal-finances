package dev.mvvasilev.finances.persistence.dtos;

import java.time.LocalDateTime;

public interface RawStatementDTO {

    Long getId();

    String getName();

    LocalDateTime getTimeCreated();

}
