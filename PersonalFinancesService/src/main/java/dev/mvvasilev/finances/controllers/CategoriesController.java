package dev.mvvasilev.finances.controllers;

import dev.mvvasilev.common.controller.AbstractRestController;
import dev.mvvasilev.common.web.APIResponseDTO;
import dev.mvvasilev.common.web.CrudResponseDTO;
import dev.mvvasilev.finances.dtos.*;
import dev.mvvasilev.finances.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/categories")
public class CategoriesController extends AbstractRestController {

    final private CategoryService categoryService;

    @Autowired
    public CategoriesController(CategoryService categoryService) {
        this.categoryService = categoryService;
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
    public ResponseEntity<APIResponseDTO<CrudResponseDTO>> createCategorizationRule(
            @PathVariable("categoryId") Long categoryId,
            @RequestBody Collection<CreateCategorizationDTO> dto
    ) {
        return created(categoryService.createCategorizationRule(categoryId, dto));
    }

    @DeleteMapping("/{categoryId}/rules/{ruleId}")
    @PreAuthorize("@authService.isOwner(#categoryId, T(dev.mvvasilev.finances.entity.TransactionCategory))")
    public ResponseEntity<APIResponseDTO<CrudResponseDTO>> deleteCategorizationRule(
            @PathVariable("categoryId") Long categoryId,
            @PathVariable("ruleId") Long ruleId
    ) {
        return deleted(categoryService.deleteCategorizationRule(ruleId));
    }

    @PostMapping("/categorize")
    public ResponseEntity<APIResponseDTO<Object>> categorizeTransactions(Authentication authentication) {
        categoryService.categorizeForUser(Integer.parseInt(authentication.getName()));
        return emptySuccess();
    }

}
