package dev.mvvasilev.finances.persistence;

import dev.mvvasilev.finances.entity.ProcessedTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ProcessedTransactionRepository extends JpaRepository<ProcessedTransaction, Long> {

    @Query(value = "SELECT * FROM transactions.processed_transaction WHERE user_id = :userId", nativeQuery = true)
    Collection<ProcessedTransaction> fetchForUser(@Param("userId") int userId);
}
