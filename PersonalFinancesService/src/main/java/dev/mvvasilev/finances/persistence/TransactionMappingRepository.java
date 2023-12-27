package dev.mvvasilev.finances.persistence;

import dev.mvvasilev.finances.entity.TransactionMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface TransactionMappingRepository extends JpaRepository<TransactionMapping, Long> {

    @Query(
            value = """
                    SELECT tm.*
                    FROM transactions.transaction_mappings AS tm
                    JOIN transactions.raw_transaction_value_group AS rtvg ON rtvg.id = tm.raw_transaction_value_group_id
                    JOIN transactions.statements AS s ON s.id = rtvg.statement_id
                    WHERE s.id = :statementId
                    """,
            nativeQuery = true
    )
    Collection<TransactionMapping> fetchTransactionMappingsWithStatementId(@Param("statementId") Long statementId);

}
