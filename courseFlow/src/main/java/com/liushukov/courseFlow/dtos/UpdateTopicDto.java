package com.liushukov.courseFlow.dtos;

import jakarta.validation.constraints.Size;

public record UpdateTopicDto(
        @Size(min = 5, max = 100, message = "name should contain at least 5 symbols")
        String name,
        @Size(min = 10, max = 600, message = "description should contain at least 10 symbols")
        String description
) {
}
