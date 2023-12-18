package dev.mvvasilev.finances.entity;

import dev.mvvasilev.common.data.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(schema = "transactions")
public class ProcessedTransaction extends AbstractEntity {

    private String description;

    private Integer userId;

    private Double amount;

    private boolean isInflow;

    private Long categoryId;

    private LocalDateTime timestamp;

    // private Long transactionMappingId;

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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
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

//    public Long getTransactionMappingId() {
//        return transactionMappingId;
//    }
//
//    public void setTransactionMappingId(Long transactionMappingId) {
//        this.transactionMappingId = transactionMappingId;
//    }
}
