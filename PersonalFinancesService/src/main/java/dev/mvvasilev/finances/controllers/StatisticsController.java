package dev.mvvasilev.finances.controllers;

import dev.mvvasilev.common.controller.AbstractRestController;
import dev.mvvasilev.common.web.APIResponseDTO;
import dev.mvvasilev.finances.dtos.SpendingByCategoriesDTO;
import dev.mvvasilev.finances.dtos.SpendingOverTimeByCategoryDTO;
import dev.mvvasilev.finances.enums.TimePeriod;
import dev.mvvasilev.finances.services.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Collection;

@RestController
@RequestMapping("/statistics")
public class StatisticsController extends AbstractRestController {

    private final StatisticsService statisticsService;

    @Autowired
    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/timePeriods")
    public ResponseEntity<APIResponseDTO<TimePeriod[]>> fetchTimePeriods() {
        return ok(TimePeriod.values());
    }

    @GetMapping("/totalSpendingByCategory")
    @PreAuthorize("@authService.isOwner(#categoryId, T(dev.mvvasilev.finances.entity.TransactionCategory))")
    public ResponseEntity<APIResponseDTO<SpendingByCategoriesDTO>> fetchSpendingByCategory(
            Long[] categoryId,
            @RequestParam(defaultValue = "1970-01-01T00:00:00") LocalDateTime from,
            @RequestParam(defaultValue = "2099-01-01T00:00:00") LocalDateTime to,
            @RequestParam(defaultValue = "false") Boolean includeUncategorized
    ) {
        return ok(statisticsService.spendingByCategory(categoryId, from, to));
    }

    @GetMapping("/spendingOverTimeByCategory")
    @PreAuthorize("@authService.isOwner(#categoryId, T(dev.mvvasilev.finances.entity.TransactionCategory))")
    public ResponseEntity<APIResponseDTO<SpendingOverTimeByCategoryDTO>> fetchSpendingOverTimeByCategory(
            Long[] categoryId,
            @RequestParam(defaultValue = "DAILY") TimePeriod period,
            @RequestParam(defaultValue = "1970-01-01T00:00:00") LocalDateTime from,
            @RequestParam(defaultValue = "2099-01-01T00:00:00") LocalDateTime to,
            @RequestParam(defaultValue = "false") Boolean includeUncategorized
    ) {
        return ok(statisticsService.spendingByCategoryOverTime(categoryId, period, from, to));
    }

    @GetMapping("/sumByCategory")
    @PreAuthorize("@authService.isOwner(#categoryId, T(dev.mvvasilev.finances.entity.TransactionCategory))")
    public ResponseEntity<APIResponseDTO<Double>> sum(
            Long[] categoryId,
            @RequestParam(defaultValue = "1970-01-01T00:00:00") LocalDateTime from,
            @RequestParam(defaultValue = "2099-01-01T00:00:00") LocalDateTime to,
            @RequestParam(defaultValue = "false") Boolean includeUncategorized
    ) {
        return ok(statisticsService.sumByCategory(categoryId, from, to, includeUncategorized));
    }

}
