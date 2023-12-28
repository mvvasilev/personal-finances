package dev.mvvasilev.finances.controllers;

import dev.mvvasilev.common.controller.AbstractRestController;
import dev.mvvasilev.common.web.APIResponseDTO;
import dev.mvvasilev.finances.dtos.ProcessedTransactionDTO;
import dev.mvvasilev.finances.dtos.ProcessedTransactionFieldDTO;
import dev.mvvasilev.finances.enums.ProcessedTransactionField;
import dev.mvvasilev.finances.services.ProcessedTransactionService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collection;

@RestController
@RequestMapping("/processed-transactions")
public class ProcessedTransactionsController extends AbstractRestController {

    final private ProcessedTransactionService processedTransactionService;

    @Autowired
    public ProcessedTransactionsController(ProcessedTransactionService processedTransactionService) {
        this.processedTransactionService = processedTransactionService;
    }

    @GetMapping("/fields")
    public ResponseEntity<APIResponseDTO<Collection<ProcessedTransactionFieldDTO>>> fetchFields() {
        return ok(
                Arrays.stream(ProcessedTransactionField.values())
                        .map(field -> new ProcessedTransactionFieldDTO(field, field.type()))
                        .toList()
        );
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
