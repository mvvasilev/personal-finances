package dev.mvvasilev.common.controller;

import dev.mvvasilev.common.web.APIErrorDTO;
import dev.mvvasilev.common.web.APIResponseDTO;

import java.util.List;

public class AbstractRestController {

    protected <T> APIResponseDTO<T> withStatus(int statusCode, String statusText, T body) {
        return new APIResponseDTO<>(body, null, statusCode, statusText);
    }

    protected <T> APIResponseDTO<T> withSingleError(int statusCode, String statusText, String errorMessage, String errorCode, String stacktrace) {
        return new APIResponseDTO<>(null, List.of(new APIErrorDTO(errorMessage, errorCode, stacktrace)), statusCode, statusText);
    }

    protected <T> APIResponseDTO<T> ok(T body) {
        return withStatus(200, "ok", body);
    }

}
