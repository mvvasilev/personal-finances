package dev.mvvasilev.statements.controllers;

import dev.mvvasilev.common.controller.AbstractRestController;
import dev.mvvasilev.common.web.APIResponseDTO;
import dev.mvvasilev.common.web.CrudResponseDTO;
import dev.mvvasilev.statements.dto.CreateTransactionMappingDTO;
import dev.mvvasilev.statements.dto.TransactionMappingDTO;
import dev.mvvasilev.statements.dto.TransactionValueGroupDTO;
import dev.mvvasilev.statements.dto.UploadedStatementDTO;
import dev.mvvasilev.statements.service.StatementsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;

@RestController
@RequestMapping("/statements")
public class StatementsController extends AbstractRestController {

    final private StatementsService statementsService;

    @Autowired
    public StatementsController(StatementsService statementsService) {
        this.statementsService = statementsService;
    }

    @GetMapping
    public ResponseEntity<APIResponseDTO<Collection<UploadedStatementDTO>>> fetchStatements(Authentication authentication) {
        return ok(statementsService.fetchStatementsForUser(Integer.parseInt(authentication.getName())));
    }

    @GetMapping("/{statementId}/transactionValueGroups")
    @PreAuthorize("@authService.isOwner(#statementId, T(dev.mvvasilev.finances.entity.RawStatement))")
    public ResponseEntity<APIResponseDTO<Collection<TransactionValueGroupDTO>>> fetchTransactionValueGroups(
            @PathVariable("statementId") Long statementId
    ) {
        return ok(statementsService.fetchTransactionValueGroupsForUserStatement(statementId));
    }

    @GetMapping("/{statementId}/mappings")
    @PreAuthorize("@authService.isOwner(#statementId, T(dev.mvvasilev.finances.entity.RawStatement))")
    public ResponseEntity<APIResponseDTO<Collection<TransactionMappingDTO>>> fetchTransactionMappings(
            @PathVariable("statementId") Long statementId
    ) {
        return ok(statementsService.fetchMappingsForStatement(statementId));
    }

    @PostMapping("/{statementId}/mappings")
    @PreAuthorize("@authService.isOwner(#statementId, T(dev.mvvasilev.finances.entity.RawStatement))")
    public ResponseEntity<APIResponseDTO<Collection<CrudResponseDTO>>> createTransactionMappings(
            @PathVariable("statementId") Long statementId,
            @RequestBody Collection<CreateTransactionMappingDTO> body
    ) {
        return ok(statementsService.createTransactionMappingsForStatement(statementId, body));
    }


    @PostMapping("/{statementId}/process")
    @PreAuthorize("@authService.isOwner(#statementId, T(dev.mvvasilev.finances.entity.RawStatement))")
    public ResponseEntity<APIResponseDTO<Object>> processTransactions(@PathVariable("statementId") Long statementId, Authentication authentication) {
        statementsService.processStatement(statementId, Integer.parseInt(authentication.getName()));
        return emptySuccess();
    }

    @DeleteMapping("/{statementId}")
    @PreAuthorize("@authService.isOwner(#statementId, T(dev.mvvasilev.finances.entity.RawStatement))")
    public ResponseEntity<APIResponseDTO<Object>> deleteStatement(@PathVariable("statementId")  Long statementId) {
        statementsService.deleteStatement(statementId);

        return emptySuccess();
    }

    @PostMapping(value = "/uploadSheet", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<APIResponseDTO<Object>> uploadStatement(@RequestParam("file") MultipartFile file, Authentication authentication) throws IOException {
        statementsService.uploadStatementFromExcelSheetForUser(
                file.getOriginalFilename(),
                file.getContentType(),
                file.getInputStream(),
                Integer.parseInt(authentication.getName())
        );

        return emptySuccess();
    }

}
