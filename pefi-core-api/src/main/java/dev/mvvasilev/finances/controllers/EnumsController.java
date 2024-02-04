package dev.mvvasilev.finances.controllers;

import dev.mvvasilev.common.controller.AbstractRestController;
import dev.mvvasilev.common.enums.ProcessedTransactionField;
import dev.mvvasilev.common.web.APIResponseDTO;
import dev.mvvasilev.finances.dtos.CategorizationRuleDTO;
import dev.mvvasilev.finances.dtos.ProcessedTransactionFieldDTO;
import dev.mvvasilev.finances.enums.CategorizationRule;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collection;

@RestController
@RequestMapping("/enums")
public class EnumsController extends AbstractRestController {

    @GetMapping("/category-rules")
    public ResponseEntity<APIResponseDTO<Collection<CategorizationRuleDTO>>> fetchCategorizationRules() {
        return ok(
                Arrays.stream(CategorizationRule.values()).map(r -> new CategorizationRuleDTO(
                        r,
                        r.applicableForType()
                )).toList()
        );
    }

    @GetMapping("/processed-transaction-fields")
    public ResponseEntity<APIResponseDTO<Collection<ProcessedTransactionFieldDTO>>> fetchFields() {
        return ok(
                Arrays.stream(ProcessedTransactionField.values())
                        .map(field -> new ProcessedTransactionFieldDTO(field, field.type()))
                        .toList()
        );
    }

}
