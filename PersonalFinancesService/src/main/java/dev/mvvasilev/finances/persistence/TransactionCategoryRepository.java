package dev.mvvasilev.finances.persistence;

import dev.mvvasilev.finances.dtos.CategoryDTO;
import dev.mvvasilev.finances.entity.TransactionCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface TransactionCategoryRepository extends JpaRepository<TransactionCategory, Long> {
    @Query(value = "SELECT * FROM categories.transaction_category WHERE user_id = :userId", nativeQuery = true)
    Collection<TransactionCategory> fetchTransactionCategoriesWithUserId(@Param("userId") int userId);

    @Query(value = "UPDATE categories.transaction_category SET name = :name WHERE id = :categoryId", nativeQuery = true)
    int updateTransactionCategoryName(@Param("categoryId") Long categoryId, @Param("name") String name);
}
