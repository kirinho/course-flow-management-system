package com.liushukov.courseFlow.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LessonCreateDto(
        @NotNull(message = "title is mandatory")
        @Size(max = 255, message = "title size should be up to 255 characters")
        String title,
        @NotNull(message = "description is mandatory")
        @Size(max = 255, message = "size for description should be up to 255 characters")
        String description,
        @NotNull(message = "position is mandatory")
        Integer position,
        @NotNull(message = "content is mandatory")
        String content
) {
}
