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

    @Query(
            value = """
                    WITH RECURSIVE
                        childCats AS (
                            SELECT root.*
                            FROM categories.categorization AS root
                            WHERE root.category_id = :categoryId
                    
                            UNION ALL
                    
                            SELECT c.*
                            FROM categories.categorization AS c, childCats
                            WHERE childCats.right_categorization_id = c.id OR childCats.left_categorization_id = c.id
                        )
                    SELECT DISTINCT * FROM childCats;
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
