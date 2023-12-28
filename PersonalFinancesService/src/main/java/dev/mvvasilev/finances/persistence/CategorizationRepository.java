package dev.mvvasilev.finances.persistence;

import dev.mvvasilev.finances.dtos.CategorizationDTO;
import dev.mvvasilev.finances.entity.Categorization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface CategorizationRepository extends JpaRepository<Categorization, Long> {

    // We fetch only the ones with non-null category ids
    // because ones with null category are used in AND, OR or NOT logical operations
    @Query(
            value = """
                    SELECT cat.*
                    FROM categories.categorization AS cat
                    WHERE user_id = :userId AND category_id IS NOT NULL
                    """,
            nativeQuery = true
    )
    Collection<Categorization> fetchForUser(@Param("userId") int userId);

    // TODO: Use Recursive CTE
    @Query(
            value = """
                    WITH RECURSIVE cats AS (
                        SELECT cat.*
                        FROM categories.categorization AS cat
                        WHERE cat.category_id = :categoryId
                        
                        UNION ALL
                        
                        SELECT l.*
                        FROM categories.categorization AS l
                        JOIN cats ON cats.`left` = l.id
                        
                        UNION ALL
                        
                        SELECT r.*
                        FROM categories.categorization AS r
                        JOIN cats ON cats.`right` = r.id
                    )
                    SELECT * FROM cats;
                    """,
            nativeQuery = true
    )
    Collection<Categorization> fetchForCategory(@Param("categoryId") Long categoryId);

    @Query(
            value = """
                    DELETE FROM categories.categorization WHERE category_id = :categoryId
                    """,
            nativeQuery = true
    )
    @Modifying
    int deleteAllForCategory(@Param("categoryId") Long categoryId);
}
