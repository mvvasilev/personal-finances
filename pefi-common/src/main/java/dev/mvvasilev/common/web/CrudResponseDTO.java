package dev.mvvasilev.common.web;

public record CrudResponseDTO (
        Long createdId,
        Integer affectedRows

) {
}
