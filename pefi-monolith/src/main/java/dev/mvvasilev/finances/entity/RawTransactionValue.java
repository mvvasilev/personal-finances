package dev.mvvasilev.finances.entity;

import dev.mvvasilev.common.data.AbstractEntity;
import dev.mvvasilev.finances.enums.ProcessedTransactionField;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(schema = "transactions")
public class RawTransactionValue extends AbstractEntity {

    private Long groupId;

    private String stringValue;

    private LocalDateTime timestampValue;

    private Double numericValue;

    private Boolean booleanValue;

    private int rowIndex;

    public RawTransactionValue() {
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public LocalDateTime getTimestampValue() {
        return timestampValue;
    }

    public void setTimestampValue(LocalDateTime timestampValue) {
        this.timestampValue = timestampValue;
    }

    public Double getNumericValue() {
        return numericValue;
    }

    public void setNumericValue(Double numericValue) {
        this.numericValue = numericValue;
    }

    public Boolean getBooleanValue() {
        return booleanValue;
    }

    public void setBooleanValue(Boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }
}
