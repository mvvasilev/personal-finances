package dev.mvvasilev.finances.dtos;

import java.util.Collection;

public record ImportExportCategoriesDTO(
        Collection<CategoryDTO> categories,

        Collection<CategorizationDTO> categorizationRules
) {}
