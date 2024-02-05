package dev.mvvasilev.common.dto;

import java.time.LocalDateTime;

public class KafkaProcessedTransactionDTO {

    private String description;

    private Integer userId;

    private Double amount;

    private boolean isInflow;

    private LocalDateTime timestamp;

    private Long statementId;

    public KafkaProcessedTransactionDTO() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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

    public Long getStatementId() {
        return statementId;
    }

    public void setStatementId(Long statementId) {
        this.statementId = statementId;
    }
}
