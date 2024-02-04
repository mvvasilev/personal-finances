package dev.mvvasilev.widgets.dtos;

import dev.mvvasilev.widgets.enums.WidgetType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.util.List;

public record CreateUpdateWidgetDTO(
        @Length(max = 255, message = "Length of name cannot be more than 255 characters.")
        @NotNull(message = "Widget must have name.")
        String name,
        @Min(value = 0, message = "Horizontal position of widget cannot be less than 0.")
        Integer positionX,
        @Min(value = 0, message = "Vertical position of widget cannot be less than 0.")
        Integer positionY,
        @Min(value = 1, message = "Horizontal size of widget cannot be less than 1.")
        Integer sizeX,
        @Min(value = 1, message = "Vertical size of widget cannot be less than 1.")
        Integer sizeY,
        @NotNull(message = "Widget must have type.")
        WidgetType type,
        @NotNull(message = "Widget must have parameters, even if empty.")
        List<CreateWidgetParameterDTO> parameters
) {}
