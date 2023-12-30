package dev.mvvasilev.finances.services;

import dev.mvvasilev.common.data.AbstractEntity;
import dev.mvvasilev.finances.dtos.CategoryDTO;
import dev.mvvasilev.finances.dtos.SpendingByCategoryDTO;
import dev.mvvasilev.finances.persistence.StatisticsRepository;
import dev.mvvasilev.finances.persistence.TransactionCategoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class StatisticsService {

    private final TransactionCategoryRepository transactionCategoryRepository;

    private final StatisticsRepository statisticsRepository;

    public StatisticsService(TransactionCategoryRepository transactionCategoryRepository, StatisticsRepository statisticsRepository) {
        this.transactionCategoryRepository = transactionCategoryRepository;
        this.statisticsRepository = statisticsRepository;
    }

    public SpendingByCategoryDTO spendingByCategory(LocalDateTime from, LocalDateTime to, int userId) {
        final var categories = transactionCategoryRepository.fetchTransactionCategoriesWithUserId(userId);
        final var spendingByCategory = statisticsRepository.fetchSpendingByCategory(
                from,
                to,
                categories.stream().map(AbstractEntity::getId).toList()
        );

        return new SpendingByCategoryDTO(
                categories.stream().map(c -> new CategoryDTO(c.getId(), c.getName(), null)).toList(),
                spendingByCategory
        );
    }
}
