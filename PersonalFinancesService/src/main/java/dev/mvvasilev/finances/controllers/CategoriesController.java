package dev.mvvasilev.finances.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mvvasilev.common.controller.AbstractRestController;
import dev.mvvasilev.common.web.APIResponseDTO;
import dev.mvvasilev.common.web.CrudResponseDTO;
import dev.mvvasilev.finances.dtos.*;
import dev.mvvasilev.finances.enums.CategorizationRule;
import dev.mvvasilev.finances.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;

@RestController
@RequestMapping("/categories")
public class CategoriesController extends AbstractRestController {

    final private CategoryService categoryService;

    final private ObjectMapper objectMapper;

    @Autowired
    public CategoriesController(CategoryService categoryService, ObjectMapper objectMapper) {
        this.categoryService = categoryService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/rules")
    public ResponseEntity<APIResponseDTO<Collection<CategorizationRuleDTO>>> fetchCategorizationRules() {
        return ok(
                Arrays.stream(CategorizationRule.values()).map(r -> new CategorizationRuleDTO(
                        r,
                        r.applicableForType()
                )).toList()
        );
    }

    @PostMapping
    public ResponseEntity<APIResponseDTO<CrudResponseDTO>> createCategory(
            @RequestBody CreateCategoryDTO dto,
            Authentication authentication
    ) {
        return created(categoryService.createForUser(dto, Integer.parseInt(authentication.getName())));
    }

    @GetMapping
    public ResponseEntity<APIResponseDTO<Collection<CategoryDTO>>> listCategories(Authentication authentication) {
        return ok(categoryService.listForUser(Integer.parseInt(authentication.getName())));
    }

    @PutMapping("/{categoryId}")
    @PreAuthorize("@authService.isOwner(#categoryId, T(dev.mvvasilev.finances.entity.TransactionCategory))")
    public ResponseEntity<APIResponseDTO<CrudResponseDTO>> updateCategory(
            @PathVariable("categoryId") Long categoryId,
            @RequestBody UpdateCategoryDTO dto
    ) {
        return updated(categoryService.update(categoryId, dto));
    }

    @DeleteMapping("/{categoryId}")
    @PreAuthorize("@authService.isOwner(#categoryId, T(dev.mvvasilev.finances.entity.TransactionCategory))")
    public ResponseEntity<APIResponseDTO<CrudResponseDTO>> deleteCategory(@PathVariable("categoryId") Long categoryId) {
        return deleted(categoryService.delete(categoryId));
    }

    @GetMapping("/{categoryId}/rules")
    @PreAuthorize("@authService.isOwner(#categoryId, T(dev.mvvasilev.finances.entity.TransactionCategory))")
    public ResponseEntity<APIResponseDTO<Collection<CategorizationDTO>>> fetchCategorizationRules(@PathVariable("categoryId") Long categoryId) {
        return ok(categoryService.fetchCategorizationRules(categoryId));
    }

    @PostMapping("/{categoryId}/rules")
    @PreAuthorize("@authService.isOwner(#categoryId, T(dev.mvvasilev.finances.entity.TransactionCategory))")
    public ResponseEntity<APIResponseDTO<Collection<CrudResponseDTO>>> createCategorizationRules(
            @PathVariable("categoryId") Long categoryId,
            @RequestBody Collection<CreateCategorizationDTO> dto,
            Authentication authentication
    ) {
        return created(categoryService.createCategorizationRules(categoryId, Integer.parseInt(authentication.getName()), dto));
    }

    @GetMapping("/export")
    public ResponseEntity<APIResponseDTO<byte[]>> exportCategories(Authentication authentication) throws JsonProcessingException {
        var json = objectMapper.writeValueAsString(categoryService.exportCategoriesForUser(Integer.parseInt(authentication.getName())));

        return file(json.getBytes(Charset.defaultCharset()), MediaType.APPLICATION_JSON);
    }

    @PostMapping("/import")
    public ResponseEntity<APIResponseDTO<Collection<CrudResponseDTO>>> importCategories(
            @RequestParam("file") MultipartFile file,
            @RequestParam("deleteExisting") Boolean deleteExisting,
            Authentication authentication
    ) throws IOException {
        return created(categoryService.importCategoriesForUser(
                objectMapper.readValue(file.getBytes(), ImportExportCategoriesDTO.class),
                deleteExisting,
                Integer.parseInt(authentication.getName())
        ));
    }

    @PostMapping("/categorize")
    public ResponseEntity<APIResponseDTO<Object>> categorizeTransactions(Authentication authentication) {
        categoryService.categorizeForUser(Integer.parseInt(authentication.getName()));
        return emptySuccess();
    }

}
