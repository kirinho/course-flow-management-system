package com.liushukov.courseFlow.dtos;

import jakarta.validation.constraints.NotNull;

public record ModuleCreateDto(
        @NotNull(message = "name for module is mandatory")
        String name,
        @NotNull(message = "description for module is mandatory")
        String description,
        @NotNull(message = "position is mandatory")
        Integer position
) {
}
