package dev.mvvasilev.statements.entity;

import dev.mvvasilev.common.data.AbstractEntity;
import dev.mvvasilev.common.enums.ProcessedTransactionField;
import dev.mvvasilev.statements.enums.MappingConversionType;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(schema = "transactions")
public class TransactionMapping extends AbstractEntity {

    private Long rawTransactionValueGroupId;

    @Convert(converter = ProcessedTransactionField.JpaConverter.class)
    private ProcessedTransactionField processedTransactionField;

    @Convert(converter = MappingConversionType.JpaConverter.class)
    private MappingConversionType conversionType;

    private String trueBranchStringValue;

    private String falseBranchStringValue;

    private String timestampPattern;

    public TransactionMapping() {
    }

    public Long getRawTransactionValueGroupId() {
        return rawTransactionValueGroupId;
    }

    public void setRawTransactionValueGroupId(Long rawTransactionValueGroupId) {
        this.rawTransactionValueGroupId = rawTransactionValueGroupId;
    }

    public ProcessedTransactionField getProcessedTransactionField() {
        return processedTransactionField;
    }

    public void setProcessedTransactionField(ProcessedTransactionField processedTransactionField) {
        this.processedTransactionField = processedTransactionField;
    }

    public MappingConversionType getConversionType() {
        return conversionType;
    }

    public void setConversionType(MappingConversionType conversionType) {
        this.conversionType = conversionType;
    }

    public String getTrueBranchStringValue() {
        return trueBranchStringValue;
    }

    public void setTrueBranchStringValue(String trueBranchStringValue) {
        this.trueBranchStringValue = trueBranchStringValue;
    }

    public String getFalseBranchStringValue() {
        return falseBranchStringValue;
    }

    public void setFalseBranchStringValue(String falseBranchStringValue) {
        this.falseBranchStringValue = falseBranchStringValue;
    }

    public String getTimestampPattern() {
        return timestampPattern;
    }

    public void setTimestampPattern(String timestampPattern) {
        this.timestampPattern = timestampPattern;
    }
}
