package com.liushukov.courseFlow.dtos;

import jakarta.validation.constraints.NotNull;

public record ModuleUpdateDto(
        String name,
        String description,
        @NotNull(message = "position is mandatory")
        Integer position
) {
}
