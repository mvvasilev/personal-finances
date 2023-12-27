package dev.mvvasilev.finances.persistence;

import dev.mvvasilev.finances.entity.ProcessedTransaction;
import dev.mvvasilev.finances.entity.ProcessedTransactionCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessedTransactionCategoryRepository extends JpaRepository<ProcessedTransactionCategory, Long> {
}
