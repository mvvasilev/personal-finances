package dev.mvvasilev.finances.entity;

import dev.mvvasilev.common.data.AbstractEntity;
import dev.mvvasilev.common.data.UserOwned;
import dev.mvvasilev.finances.enums.ProcessedTransactionField;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(schema = "transactions")
public class ProcessedTransaction extends AbstractEntity implements UserOwned {

    // This const is a result of the limitations of the JVM.
    // It is impossible to refer to either a field, or method of a class statically.
    // Because of this, it is very difficult to tie the ProcessedTransactionField values to the actual class fields they represent.
    // To resolve this imperfection, this const lives here, in plain view, so when one of the fields is changed,
    // hopefully the programmer remembers to change the value inside as well.
    public static final Map<ProcessedTransactionField, String> FIELD_NAMES = Map.of(
            ProcessedTransactionField.DESCRIPTION, "description",
            ProcessedTransactionField.AMOUNT, "amount",
            ProcessedTransactionField.IS_INFLOW, "isInflow",
            ProcessedTransactionField.TIMESTAMP, "timestamp"
    );

    private String description;

    private Integer userId;

    private Double amount;

    private boolean isInflow;

    private LocalDateTime timestamp;

    private Long statementId;

    public ProcessedTransaction() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public boolean isInflow() {
        return isInflow;
    }

    public void setInflow(boolean inflow) {
        isInflow = inflow;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Long getStatementId() {
        return statementId;
    }

    public void setStatementId(Long statementId) {
        this.statementId = statementId;
    }
}
