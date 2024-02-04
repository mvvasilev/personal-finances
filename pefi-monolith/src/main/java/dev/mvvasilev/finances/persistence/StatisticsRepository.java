package dev.mvvasilev.finances.persistence;

import dev.mvvasilev.finances.dtos.SpendingOverTimeByCategoryDTO;
import dev.mvvasilev.finances.enums.TimePeriod;
import dev.mvvasilev.finances.persistence.dtos.SpendingOverTimeDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
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

    public Map<Long, Double> fetchSpendingByCategory(Long[] categoryId, LocalDateTime from, LocalDateTime to) {
        Query nativeQuery = entityManager.createNativeQuery(
        """
                SELECT ptc.category_id AS category_id, ROUND(CAST(SUM(pt.amount) AS NUMERIC), 2) AS total_spending
                FROM transactions.processed_transaction AS pt
                JOIN categories.processed_transaction_category AS ptc ON ptc.processed_transaction_id = pt.id
                WHERE
                        pt.is_inflow = FALSE
                    AND ptc.category_id = any(?1)
                    AND (pt.timestamp BETWEEN ?2 AND ?3)
                GROUP BY ptc.category_id
                ORDER BY total_spending DESC;
                """,
                Tuple.class
        );

        nativeQuery.setParameter(1, categoryId);
        nativeQuery.setParameter(2, from);
        nativeQuery.setParameter(3, to);

        //noinspection unchecked
        return (Map<Long, Double>) nativeQuery.getResultStream().collect(Collectors.toMap(
                (Tuple tuple) -> ((Number) tuple.get("category_id")).longValue(),
                (Tuple tuple) -> ((Number) tuple.get("total_spending")).doubleValue()
        ));
    }

    public Collection<SpendingOverTimeDTO> fetchSpendingByCategoryOverTime(LocalDateTime from, LocalDateTime to, TimePeriod period, Long[] categoryId) {
        Query nativeQuery = entityManager.createNativeQuery("SELECT * FROM statistics.spending_over_time(?1, ?2, ?3, ?4) ORDER BY period_beginning_timestamp;", Tuple.class);

        nativeQuery.setParameter(1, categoryId);
        nativeQuery.setParameter(2, period.toString());
        nativeQuery.setParameter(3, from);
        nativeQuery.setParameter(4, to);


        //noinspection unchecked
        return nativeQuery.getResultStream().map(r -> new SpendingOverTimeDTO(
                ((Tuple) r).get("category_id", Long.class),
                ((Tuple) r).get("amount_for_period", Double.class),
                ((Tuple) r).get("period_beginning_timestamp", Timestamp.class).toLocalDateTime()
        )).toList();
    }

    public Double sumByCategory(Long[] categoryId, LocalDateTime from, LocalDateTime to, Boolean includeUncategorized) {
        Query nativeQuery = entityManager.createNativeQuery(
               """
               WITH transactions AS (
                   SELECT DISTINCT pt.*
                   FROM transactions.processed_transaction AS pt
                   LEFT OUTER JOIN categories.processed_transaction_category AS ptc ON pt.id = ptc.processed_transaction_id
                   WHERE (pt.timestamp BETWEEN ?2 AND ?3) AND (ptc.category_id = any(?1) OR (?4 AND ptc.category_id IS NULL))
               )
               SELECT COALESCE(SUM(pt.amount), 0) AS result
               FROM transactions AS pt
               """,
                Tuple.class
        );

        nativeQuery.setParameter(1, categoryId);
        nativeQuery.setParameter(2, from);
        nativeQuery.setParameter(3, to);
        nativeQuery.setParameter(4, includeUncategorized);

        return ((Tuple) nativeQuery.getSingleResult()).get("result", Double.class);
    }
}
