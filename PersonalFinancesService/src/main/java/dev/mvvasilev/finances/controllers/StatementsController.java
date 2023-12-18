package dev.mvvasilev.finances.controllers;

import dev.mvvasilev.common.controller.AbstractRestController;
import dev.mvvasilev.common.web.APIResponseDTO;
import dev.mvvasilev.finances.dtos.TransactionValueGroupDTO;
import dev.mvvasilev.finances.dtos.UploadedStatementDTO;
import dev.mvvasilev.finances.services.StatementsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;

@RestController
@RequestMapping("/statements")
public class StatementsController extends AbstractRestController {

    private StatementsService statementsService;

    @Autowired
    public StatementsController(StatementsService statementsService) {
        this.statementsService = statementsService;
    }

    @GetMapping
    public ResponseEntity<APIResponseDTO<Collection<UploadedStatementDTO>>> fetchStatements(Authentication authentication) {
        return ResponseEntity.ofNullable(
                ok(statementsService.fetchStatementsForUser(Integer.parseInt(authentication.getName())))
        );
    }

    @GetMapping("/{statementId}/transactionValueGroups")
    public ResponseEntity<APIResponseDTO<Collection<TransactionValueGroupDTO>>> fetchTransactionValueGroups(@PathVariable("statementId") Long statementId, Authentication authentication) {
        return ResponseEntity.ofNullable(ok(
                statementsService.fetchTransactionValueGroupsForUserStatement(statementId, Integer.parseInt(authentication.getName()))
        ));
    }

    @DeleteMapping("/{statementId}")
    public ResponseEntity<APIResponseDTO> deleteStatement(@PathVariable("statementId")  Long statementId, Authentication authentication) {
        statementsService.deleteStatement(statementId, Integer.parseInt(authentication.getName()));
        return ResponseEntity.ofNullable(ok(null));
    }

    @PostMapping(value = "/uploadSheet", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<APIResponseDTO<Integer>> uploadStatement(@RequestParam("file") MultipartFile file, Authentication authentication) throws IOException {
        statementsService.uploadStatementFromExcelSheetForUser(
                file.getOriginalFilename(),
                file.getContentType(),
                file.getInputStream(),
                Integer.parseInt(authentication.getName())
        );
        return ResponseEntity.ofNullable(ok(1));
    }

}
