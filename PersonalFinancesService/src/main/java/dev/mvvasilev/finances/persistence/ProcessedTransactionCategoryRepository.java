package dev.mvvasilev.finances.persistence;

import dev.mvvasilev.finances.entity.ProcessedTransaction;
import dev.mvvasilev.finances.entity.ProcessedTransactionCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.stream.Stream;

@Repository
public interface ProcessedTransactionCategoryRepository extends JpaRepository<ProcessedTransactionCategory, Long> {
    @Query(
            value="""
                  DELETE FROM categories.processed_transaction_category
                  WHERE processed_transaction_id IN (:transactionIds)
                  """,
            nativeQuery = true
    )
    @Modifying
    void deleteAllForTransactions(@Param("transactionIds") Collection<Long> transactionIds);
}
