package dev.mvvasilev.finances.entity;

import dev.mvvasilev.common.data.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(schema = "widgets")
public class WidgetParameter extends AbstractEntity {

    private Long widgetId;

    private String name;

    private String stringValue;

    private Double numericValue;

    private LocalDateTime timestampValue;

    private Boolean booleanValue;

    public WidgetParameter() {
    }

    public Long getWidgetId() {
        return widgetId;
    }

    public void setWidgetId(Long widgetId) {
        this.widgetId = widgetId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public Double getNumericValue() {
        return numericValue;
    }

    public void setNumericValue(Double numericValue) {
        this.numericValue = numericValue;
    }

    public LocalDateTime getTimestampValue() {
        return timestampValue;
    }

    public void setTimestampValue(LocalDateTime timestampValue) {
        this.timestampValue = timestampValue;
    }

    public Boolean getBooleanValue() {
        return booleanValue;
    }

    public void setBooleanValue(Boolean booleanValue) {
        this.booleanValue = booleanValue;
    }
}
