package dev.mvvasilev.finances.persistence;

import dev.mvvasilev.finances.dtos.CategoryDTO;
import dev.mvvasilev.finances.entity.TransactionCategory;
import dev.mvvasilev.finances.enums.CategorizationRuleBehavior;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface TransactionCategoryRepository extends JpaRepository<TransactionCategory, Long> {
    @Query(value = "SELECT * FROM categories.transaction_category WHERE user_id = :userId", nativeQuery = true)
    Collection<TransactionCategory> fetchTransactionCategoriesWithUserId(@Param("userId") int userId);

    @Query(value = """
                   UPDATE TransactionCategory tc
                   SET tc.name = :name, tc.ruleBehavior = :ruleBehavior 
                   WHERE tc.id = :categoryId
                   """
    )
    @Modifying
    int updateTransactionCategoryName(
            @Param("categoryId") Long categoryId,
            @Param("name") String name,
            @Param("ruleBehavior") CategorizationRuleBehavior ruleBehavior
    );

    @Query(value = """
                   SELECT tc.*
                   FROM categories.processed_transaction_category AS ptc
                   JOIN categories.transaction_category AS tc ON tc.id = ptc.category_id
                   WHERE ptc.processed_transaction_id = :transactionId
                   """,
            nativeQuery = true
    )
    Collection<TransactionCategory> fetchCategoriesForTransaction(@Param("transactionId") Long transactionId);
}
