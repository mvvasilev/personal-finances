package dev.mvvasilev.finances.persistence;

import dev.mvvasilev.finances.dtos.TransactionCategoryDTO;
import dev.mvvasilev.finances.entity.ProcessedTransaction;
import dev.mvvasilev.finances.entity.ProcessedTransactionCategory;
import dev.mvvasilev.finances.entity.TransactionCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ProcessedTransactionRepository extends JpaRepository<ProcessedTransaction, Long> {

    @Query(value = "SELECT * FROM transactions.processed_transaction WHERE user_id = :userId", nativeQuery = true)
    Collection<ProcessedTransaction> fetchForUser(@Param("userId") int userId);

    Page<ProcessedTransaction> findAllByUserId(int userId, Pageable pageable);

    @Query(value = "DELETE FROM transactions.processed_transaction WHERE statement_id = :statementId", nativeQuery = true)
    @Modifying
    void deleteProcessedTransactionsForStatement(@Param("statementId") Long statementId);
}
