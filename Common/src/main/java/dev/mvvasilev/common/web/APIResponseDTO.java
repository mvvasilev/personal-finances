package dev.mvvasilev.common.web;

import java.util.Collection;

public record APIResponseDTO<T>(T result, Collection<APIErrorDTO> errors, int statusCode, String statusText) { }
