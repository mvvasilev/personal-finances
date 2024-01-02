package dev.mvvasilev.finances.services;

import dev.mvvasilev.common.data.AbstractEntity;
import dev.mvvasilev.finances.dtos.CategoryDTO;
import dev.mvvasilev.finances.dtos.SpendingByCategoriesDTO;
import dev.mvvasilev.finances.dtos.SpendingByCategoryDTO;
import dev.mvvasilev.finances.dtos.SpendingOverTimeByCategoryDTO;
import dev.mvvasilev.finances.enums.TimePeriod;
import dev.mvvasilev.finances.persistence.StatisticsRepository;
import dev.mvvasilev.finances.persistence.TransactionCategoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class StatisticsService {

    private final TransactionCategoryRepository transactionCategoryRepository;

    private final StatisticsRepository statisticsRepository;

    public StatisticsService(TransactionCategoryRepository transactionCategoryRepository, StatisticsRepository statisticsRepository) {
        this.transactionCategoryRepository = transactionCategoryRepository;
        this.statisticsRepository = statisticsRepository;
    }

    public SpendingByCategoriesDTO spendingByCategory(Long[] categoryId, LocalDateTime from, LocalDateTime to) {
        final var categories = transactionCategoryRepository.findAllById(Arrays.stream(categoryId).toList()).stream().map(c -> new CategoryDTO(c.getId(), c.getName(), null)).toList();

        final var spendingByCategory = statisticsRepository.fetchSpendingByCategory(
                categoryId,
                from,
                to
        );

        return new SpendingByCategoriesDTO(
                categories,
                spendingByCategory
        );
    }

    public SpendingOverTimeByCategoryDTO spendingByCategoryOverTime(
            Long[] categoryId,
            TimePeriod period,
            LocalDateTime from,
            LocalDateTime to
    ) {
        return new SpendingOverTimeByCategoryDTO(
                transactionCategoryRepository.findAllById(Arrays.stream(categoryId).toList()).stream().map(c -> new CategoryDTO(c.getId(), c.getName(), null)).toList(),
                period.getDuration(),
                statisticsRepository.fetchSpendingByCategoryOverTime(from, to, period, categoryId).stream().map(dto -> new SpendingByCategoryDTO(
                        dto.categoryId(),
                        dto.periodBeginningTimestamp(),
                        dto.amountForPeriod()
                )).toList()
        );
    }

    // Impose limits if necessary
    private boolean validatePeriodWithinLimits(LocalDateTime from, LocalDateTime to, TimePeriod period) {
        return switch (period) {
            // Can't request daily spending breakdown for a period longer than a month
            case DAILY -> ChronoUnit.MONTHS.between(from, to) <= 1;
            // Can't request weekly spending breakdown for a period longer than a year
            case WEEKLY -> ChronoUnit.YEARS.between(from, to) <= 1;
            // Can't request bi-weekly spending breakdown for a period longer than a year
            case BIWEEKLY -> ChronoUnit.YEARS.between(from, to) <= 1;
            // Can't request monthly spending breakdown for a period longer than 3 years
            case MONTHLY -> ChronoUnit.YEARS.between(from, to) <= 3;
            // Can't request quarterly spending breakdown for a period longer than 5 years
            case QUARTERLY -> ChronoUnit.YEARS.between(from, to) <= 5;
            // Can't request yearly spending breakdown for a period longer than 30 years
            case YEARLY -> ChronoUnit.YEARS.between(from, to) <= 30;
        };
    }
}
