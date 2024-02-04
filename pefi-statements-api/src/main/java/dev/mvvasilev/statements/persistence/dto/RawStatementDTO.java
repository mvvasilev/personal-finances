package dev.mvvasilev.statements.persistence.dto;

import java.time.LocalDateTime;

public interface RawStatementDTO {

    Long getId();

    String getName();

    LocalDateTime getTimeCreated();

}
