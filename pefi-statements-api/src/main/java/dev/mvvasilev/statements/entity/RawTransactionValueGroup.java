package dev.mvvasilev.statements.entity;

import dev.mvvasilev.common.data.AbstractEntity;
import dev.mvvasilev.common.enums.RawTransactionValueType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(schema = "transactions")
public class RawTransactionValueGroup extends AbstractEntity {

    private Long statementId;

    private String name;

    private RawTransactionValueType type;

    public RawTransactionValueGroup() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RawTransactionValueType getType() {
        return type;
    }

    public void setType(RawTransactionValueType type) {
        this.type = type;
    }

    public Long getStatementId() {
        return statementId;
    }

    public void setStatementId(Long statementId) {
        this.statementId = statementId;
    }
}
