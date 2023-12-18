package dev.mvvasilev.finances.persistence;

import dev.mvvasilev.finances.entity.RawTransactionValueGroup;
import dev.mvvasilev.finances.persistence.dtos.RawTransactionValueGroupDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface RawTransactionValueGroupRepository extends JpaRepository<RawTransactionValueGroup, Long> {

    @Query(value =
            "SELECT " +
                    "rtvg.id, " +
                    "rtvg.name, " +
                    "rtvg.type " +
            "FROM transactions.raw_transaction_value_group AS rtvg " +
            "JOIN transactions.raw_statement AS rs ON rtvg.statement_id = rs.id " +
            "WHERE rs.user_id = :userId AND rs.id = :statementId",
            nativeQuery = true
    )
    Collection<RawTransactionValueGroupDTO> fetchAllForStatementAndUser(@Param("statementId") Long statementId, @Param("userId") Integer userId);

}
