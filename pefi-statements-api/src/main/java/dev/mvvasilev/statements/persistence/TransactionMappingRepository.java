package dev.mvvasilev.statements.persistence;

import dev.mvvasilev.statements.entity.TransactionMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface TransactionMappingRepository extends JpaRepository<TransactionMapping, Long> {

    @Query(
            value = """
                    SELECT tm.*
                    FROM transactions.transaction_mapping AS tm
                    JOIN transactions.raw_transaction_value_group AS rtvg ON rtvg.id = tm.raw_transaction_value_group_id
                    JOIN transactions.raw_statement AS s ON s.id = rtvg.statement_id
                    WHERE s.id = :statementId
                    """,
            nativeQuery = true
    )
    Collection<TransactionMapping> fetchTransactionMappingsWithStatementId(@Param("statementId") Long statementId);

    @Query(
            value = """
                    DELETE FROM transactions.transaction_mapping AS tm
                    WHERE tm.id IN (
                        SELECT m.id
                        FROM transactions.transaction_mapping AS m
                        JOIN transactions.raw_transaction_value_group AS rtvg ON rtvg.id = m.raw_transaction_value_group_id
                        JOIN transactions.raw_statement AS s ON s.id = rtvg.statement_id
                        WHERE s.id = :statementId
                    )
                    """,
            nativeQuery = true
    )
    @Modifying
    void deleteAllForStatement(@Param("statementId") Long statementId);
}
