package dev.mvvasilev.common.web;

import org.springframework.http.ResponseEntity;

import java.util.Collection;

public record APIResponseDTO<T>(
        T result,
        Collection<APIErrorDTO> errors,
        int statusCode,
        String statusText
)
{ }
