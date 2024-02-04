package dev.mvvasilev.finances.entity;

import jakarta.persistence.*;

@Entity
@Table(schema = "categories")
public class ProcessedTransactionCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "bigserial")
    private Long id;

    @Column(nullable = false)
    private Long processedTransactionId;

    @Column(nullable = false)
    private Long categoryId;

    public ProcessedTransactionCategory() {
    }

    public ProcessedTransactionCategory(Long processedTransactionId, Long categoryId) {
        this.processedTransactionId = processedTransactionId;
        this.categoryId = categoryId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProcessedTransactionId() {
        return processedTransactionId;
    }

    public void setProcessedTransactionId(Long processedTransactionId) {
        this.processedTransactionId = processedTransactionId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}
