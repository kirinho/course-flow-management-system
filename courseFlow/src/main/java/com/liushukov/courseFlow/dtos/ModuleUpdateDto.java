package com.liushukov.courseFlow.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ModuleUpdateDto(
        String name,
        String description,
        @NotNull(message = "position is mandatory")
        @Positive(message = "position must be a positive number")
        Integer position
) {
}
