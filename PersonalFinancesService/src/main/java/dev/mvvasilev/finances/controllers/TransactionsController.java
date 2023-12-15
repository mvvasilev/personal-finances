package dev.mvvasilev.finances.controllers;

import dev.mvvasilev.common.controller.AbstractRestController;
import dev.mvvasilev.common.web.APIResponseDTO;
import dev.mvvasilev.finances.services.TransactionsService;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController("/transactions")
public class TransactionsController extends AbstractRestController {

    private TransactionsService transactionsService;

    @Autowired
    public TransactionsController(TransactionsService transactionsService) {
        this.transactionsService = transactionsService;
    }

    @PostMapping(value = "/transactions/uploadSheet", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<APIResponseDTO<Integer>> uploadTransactions(@RequestParam("file") MultipartFile file, Authentication authentication) throws IOException {
        transactionsService.uploadMultipleTransactionsFromExcelSheetForUser(
                file.getInputStream(),
                authentication.getName()
        );
        return ResponseEntity.ofNullable(ok(1));
    }

}
