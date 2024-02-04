package dev.mvvasilev.finances.controllers;

import dev.mvvasilev.common.controller.AbstractRestController;
import dev.mvvasilev.common.web.APIResponseDTO;
import dev.mvvasilev.finances.dtos.ProcessedTransactionDTO;
import dev.mvvasilev.finances.services.ProcessedTransactionService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/processed-transactions")
public class ProcessedTransactionsController extends AbstractRestController {

    final private ProcessedTransactionService processedTransactionService;

    @Autowired
    public ProcessedTransactionsController(ProcessedTransactionService processedTransactionService) {
        this.processedTransactionService = processedTransactionService;
    }

    @GetMapping
    public ResponseEntity<APIResponseDTO<Page<ProcessedTransactionDTO>>> fetchProcessedTransactions(
            Authentication authentication,
            @NotNull final Pageable pageable
    ) {
        return ok(processedTransactionService.fetchPagedProcessedTransactionsForUser(
                Integer.parseInt(authentication.getName()),
                pageable
        ));
    }
}
