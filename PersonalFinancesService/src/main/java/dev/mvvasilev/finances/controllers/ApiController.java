package dev.mvvasilev.finances.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class ApiController {

    Logger logger = LoggerFactory.getLogger(ApiController.class);

    private final ObjectMapper objectMapper;

    @Autowired
    public ApiController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostMapping("/api/user-info")
    public ResponseEntity<Authentication> userInfo(JwtAuthenticationToken authenticationToken) {
        logger.info(authenticationToken.getToken().getClaimAsString(JwtClaimNames.SUB));
        return ResponseEntity.of(Optional.of(SecurityContextHolder.getContext().getAuthentication()));
    }

}
