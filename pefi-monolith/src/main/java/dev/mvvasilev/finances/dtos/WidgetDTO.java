package dev.mvvasilev.finances.dtos;

import dev.mvvasilev.finances.enums.WidgetType;

import java.util.List;

public record WidgetDTO (
        Long id,
        String name,
        int positionX,
        int positionY,
        int sizeX,
        int sizeY,
        WidgetType type,
        List<WidgetParameterDTO> parameters
) {}
