package dev.mvvasilev.finances.entity;

import dev.mvvasilev.finances.enums.TransactionType;

import java.math.BigDecimal;

public class AccountTransaction {

    private BigDecimal amount;

    private TransactionType type;

    private String reason;

    public AccountTransaction() {
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
