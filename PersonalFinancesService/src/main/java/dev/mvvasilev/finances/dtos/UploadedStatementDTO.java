package dev.mvvasilev.finances.dtos;

import java.time.LocalDateTime;

public record UploadedStatementDTO (
        Long id,
        String name,
        LocalDateTime timeUploaded
) {}
