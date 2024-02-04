package dev.mvvasilev.finances.entity;

import dev.mvvasilev.common.data.AbstractEntity;
import dev.mvvasilev.common.data.UserOwned;
import dev.mvvasilev.finances.enums.CategorizationRule;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(schema = "categories")
public class Categorization extends AbstractEntity implements UserOwned {

    private Integer userId;

    @Convert(converter = ProcessedTransactionField.JpaConverter.class)
    private ProcessedTransactionField ruleBasedOn;

    @Convert(converter = CategorizationRule.JpaConverter.class)
    private CategorizationRule categorizationRule;

    private boolean isRoot;

    private String stringValue;

    private Double numericGreaterThan;

    private Double numericLessThan;

    private Double numericValue;

    private LocalDateTime timestampGreaterThan;

    private LocalDateTime timestampLessThan;

    private Boolean BooleanValue;

    private Long categoryId;

    private Long leftCategorizationId;

    private Long rightCategorizationId;

    public Categorization() {
    }

    @Override
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public ProcessedTransactionField getRuleBasedOn() {
        return ruleBasedOn;
    }

    public void setRuleBasedOn(ProcessedTransactionField ruleBasedOn) {
        this.ruleBasedOn = ruleBasedOn;
    }

    public CategorizationRule getCategorizationRule() {
        return categorizationRule;
    }

    public void setCategorizationRule(CategorizationRule categorizationRule) {
        this.categorizationRule = categorizationRule;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public Double getNumericGreaterThan() {
        return numericGreaterThan;
    }

    public void setNumericGreaterThan(Double numericGreaterThan) {
        this.numericGreaterThan = numericGreaterThan;
    }

    public Double getNumericLessThan() {
        return numericLessThan;
    }

    public void setNumericLessThan(Double numericLessThan) {
        this.numericLessThan = numericLessThan;
    }

    public Double getNumericValue() {
        return numericValue;
    }

    public void setNumericValue(Double numericValue) {
        this.numericValue = numericValue;
    }

    public LocalDateTime getTimestampGreaterThan() {
        return timestampGreaterThan;
    }

    public void setTimestampGreaterThan(LocalDateTime timestampGreaterThan) {
        this.timestampGreaterThan = timestampGreaterThan;
    }

    public LocalDateTime getTimestampLessThan() {
        return timestampLessThan;
    }

    public void setTimestampLessThan(LocalDateTime timestampLessThan) {
        this.timestampLessThan = timestampLessThan;
    }

    public Boolean getBooleanValue() {
        return BooleanValue;
    }

    public void setBooleanValue(Boolean BooleanValue) {
        this.BooleanValue = BooleanValue;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getLeftCategorizationId() {
        return leftCategorizationId;
    }

    public void setLeftCategorizationId(Long leftCategorizationId) {
        this.leftCategorizationId = leftCategorizationId;
    }

    public Long getRightCategorizationId() {
        return rightCategorizationId;
    }

    public void setRightCategorizationId(Long rightCategorizationId) {
        this.rightCategorizationId = rightCategorizationId;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean root) {
        isRoot = root;
    }


}
