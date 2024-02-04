package dev.mvvasilev.widgets.dtos;

import dev.mvvasilev.widgets.enums.WidgetType;

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
