package dev.mvvasilev.widgets.controllers;

import dev.mvvasilev.common.controller.AbstractRestController;
import dev.mvvasilev.common.web.APIResponseDTO;
import dev.mvvasilev.widgets.enums.WidgetType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/enums")
public class EnumsControllers extends AbstractRestController {

    @GetMapping("/widget-types")
    public ResponseEntity<APIResponseDTO<WidgetType[]>> fetchWidgetTypes() {
        return ok(WidgetType.values());
    }

}
