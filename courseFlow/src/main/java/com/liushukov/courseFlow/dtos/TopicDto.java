package com.liushukov.courseFlow.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TopicDto(
        @Size(min = 5, max = 100, message = "name should contain at least 5 symbols")
        @NotBlank(message = "name is mandatory")
        String name,
        @Size(min = 10, max = 600, message = "description should contain at least 10 symbols")
        @NotBlank(message = "description is mandatory")
        String description
) {
}
