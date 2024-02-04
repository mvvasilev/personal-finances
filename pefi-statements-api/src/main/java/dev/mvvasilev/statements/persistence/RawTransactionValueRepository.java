package dev.mvvasilev.statements.persistence;

import dev.mvvasilev.statements.entity.RawTransactionValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface RawTransactionValueRepository extends JpaRepository<RawTransactionValue, Long> {
    @Query(
            value = """
                    SELECT rtv.*
                    FROM transactions.raw_transaction_value AS rtv
                    WHERE rtv.group_id IN (:valueGroupIds)
                    """,
            nativeQuery = true
    )
    Collection<RawTransactionValue> fetchAllValuesForValueGroups(@Param("valueGroupIds") Collection<Long> values);
}
