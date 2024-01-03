package dev.mvvasilev.finances.controllers;

import dev.mvvasilev.common.controller.AbstractRestController;
import dev.mvvasilev.common.web.APIResponseDTO;
import dev.mvvasilev.common.web.CrudResponseDTO;
import dev.mvvasilev.finances.dtos.CreateUpdateWidgetDTO;
import dev.mvvasilev.finances.dtos.WidgetDTO;
import dev.mvvasilev.finances.enums.WidgetType;
import dev.mvvasilev.finances.services.WidgetService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/widgets")
public class WidgetsController extends AbstractRestController {

    private final WidgetService widgetService;

    public WidgetsController(WidgetService widgetService) {
        this.widgetService = widgetService;
    }

    @GetMapping("/types")
    public ResponseEntity<APIResponseDTO<WidgetType[]>> fetchWidgetTypes() {
        return ok(WidgetType.values());
    }

    @GetMapping
    public ResponseEntity<APIResponseDTO<Collection<WidgetDTO>>> fetchAllForUser(Authentication authentication) {
        return ok(widgetService.fetchAllForUser(Integer.parseInt(authentication.getName())));
    }

    @PostMapping
    public ResponseEntity<APIResponseDTO<CrudResponseDTO>> create(@RequestBody CreateUpdateWidgetDTO dto, Authentication authentication) {
        return created(widgetService.createWidget(dto, Integer.parseInt(authentication.getName())));
    }

    @PutMapping("/{widgetId}")
    @PreAuthorize("@authService.isOwner(#widgetId, T(dev.mvvasilev.finances.entity.Widget))")
    public ResponseEntity<APIResponseDTO<CrudResponseDTO>> update(@PathVariable Long widgetId, @RequestBody CreateUpdateWidgetDTO dto) {
        return updated(widgetService.updateWidget(widgetId, dto));
    }

    @DeleteMapping("/{widgetId}")
    @PreAuthorize("@authService.isOwner(#widgetId, T(dev.mvvasilev.finances.entity.Widget))")
    public ResponseEntity<APIResponseDTO<CrudResponseDTO>> delete(@PathVariable Long widgetId) {
        return deleted(widgetService.deleteWidget(widgetId));
    }

}
