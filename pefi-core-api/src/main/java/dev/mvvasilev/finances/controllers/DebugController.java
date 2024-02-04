package dev.mvvasilev.finances.controllers;

import dev.mvvasilev.common.controller.AbstractRestController;
import dev.mvvasilev.common.web.APIResponseDTO;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("development")
public class DebugController extends AbstractRestController {
    @GetMapping("/token")
    public ResponseEntity<APIResponseDTO<String>> fetchToken(@RequestHeader("Authorization") String authHeader) {
        return ok(authHeader);
    }
}
