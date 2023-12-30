package dev.mvvasilev.finances.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class StatisticsRepository {

    private final EntityManager entityManager;

    @Autowired
    public StatisticsRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Map<Long, Double> fetchSpendingByCategory(LocalDateTime from, LocalDateTime to, List<Long> categoryIds) {
        Query nativeQuery = entityManager.createNativeQuery(
                """
                        SELECT ptc.category_id AS category_id, ROUND(CAST(SUM(pt.amount) AS NUMERIC), 2) AS total_spending
                        FROM transactions.processed_transaction AS pt
                        JOIN categories.processed_transaction_category AS ptc ON ptc.processed_transaction_id = pt.id
                        WHERE
                                pt.is_inflow = FALSE
                            AND ptc.category_id IN (?1)
                            AND (pt.timestamp BETWEEN ?2 AND ?3)
                        GROUP BY ptc.category_id
                        ORDER BY total_spending DESC;
                        """,
                Tuple.class
        );

        nativeQuery.setParameter(1, categoryIds);
        nativeQuery.setParameter(2, from);
        nativeQuery.setParameter(3, to);

        //noinspection unchecked
        return (Map<Long, Double>) nativeQuery.getResultStream().collect(Collectors.toMap(
                (Tuple tuple) -> ((Number) tuple.get("category_id")).longValue(),
                (Tuple tuple) -> ((Number) tuple.get("total_spending")).doubleValue()
        ));
    }
}
