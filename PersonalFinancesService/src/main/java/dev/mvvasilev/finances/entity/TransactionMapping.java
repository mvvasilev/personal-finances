package dev.mvvasilev.finances.entity;

import dev.mvvasilev.common.data.AbstractEntity;
import dev.mvvasilev.common.data.UserOwned;
import dev.mvvasilev.finances.enums.ProcessedTransactionField;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(schema = "transactions")
public class TransactionMapping extends AbstractEntity {

    private Long rawTransactionValueGroupId;

    @Convert(converter = ProcessedTransactionField.JpaConverter.class)
    private ProcessedTransactionField processedTransactionField;

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
}
