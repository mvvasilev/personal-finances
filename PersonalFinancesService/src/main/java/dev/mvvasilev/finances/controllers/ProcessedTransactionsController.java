package dev.mvvasilev.finances.controllers;

import dev.mvvasilev.common.controller.AbstractRestController;
import dev.mvvasilev.common.web.APIResponseDTO;
import dev.mvvasilev.finances.enums.ProcessedTransactionField;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/processed-transactions")
public class ProcessedTransactionsController extends AbstractRestController {

    @GetMapping("/fields")
    public ResponseEntity<APIResponseDTO<ProcessedTransactionField[]>> fetchFields() {
        return ok(ProcessedTransactionField.values());
    }
}
