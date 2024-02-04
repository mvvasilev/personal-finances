package dev.mvvasilev.statements.dto;

import java.time.LocalDateTime;

public record UploadedStatementDTO (
        Long id,
        String name,
        LocalDateTime timeUploaded
) {}
