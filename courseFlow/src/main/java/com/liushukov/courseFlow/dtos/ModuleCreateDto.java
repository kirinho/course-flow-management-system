package com.liushukov.courseFlow.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ModuleCreateDto(
        @NotNull(message = "name for module is mandatory")
        String name,
        @NotNull(message = "description for module is mandatory")
        String description,
        @NotNull(message = "position is mandatory")
        @Positive(message = "position must be a positive number")
        Integer position
) {
}
