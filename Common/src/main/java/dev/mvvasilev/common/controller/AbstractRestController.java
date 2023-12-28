package dev.mvvasilev.common.controller;

import dev.mvvasilev.common.web.APIErrorDTO;
import dev.mvvasilev.common.web.APIResponseDTO;
import dev.mvvasilev.common.web.CrudResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractRestController {

    protected <T> ResponseEntity<APIResponseDTO<T>> withStatus(HttpStatus status, T body) {
        return ResponseEntity.status(status).body(new APIResponseDTO<>(body, null, status.value(), status.getReasonPhrase()));
    }

    protected <T> ResponseEntity<APIResponseDTO<T>> withSingleError(HttpStatus status, String errorMessage, String errorCode, String stacktrace) {
        return ResponseEntity.status(status).body(new APIResponseDTO<>(null, List.of(new APIErrorDTO(errorMessage, errorCode, stacktrace)), status.value(), status.getReasonPhrase()));
    }

    protected <T> ResponseEntity<APIResponseDTO<T>> ok(T body) {
        return withStatus(HttpStatus.OK, body);
    }

    protected <T> ResponseEntity<APIResponseDTO<Object>> emptySuccess() {
        return withStatus(HttpStatus.OK, null);
    }

    protected <T> ResponseEntity<APIResponseDTO<CrudResponseDTO>> created(Long id) {
        return withStatus(HttpStatus.CREATED, new CrudResponseDTO(id, null));
    }

    protected <T> ResponseEntity<APIResponseDTO<Collection<CrudResponseDTO>>> created(Collection<Long> ids) {
        return withStatus(
                HttpStatus.CREATED,
                ids.stream()
                        .map(id -> new CrudResponseDTO(id, null))
                        .collect(Collectors.toList())
        );
    }

    protected <T> ResponseEntity<APIResponseDTO<CrudResponseDTO>> updated(Integer affectedRows) {
        return withStatus(HttpStatus.OK, new CrudResponseDTO(null, affectedRows));
    }

    protected <T> ResponseEntity<APIResponseDTO<CrudResponseDTO>> deleted(Integer affectedRows) {
        return withStatus(HttpStatus.OK, new CrudResponseDTO(null, affectedRows));
    }

}
