package com.liushukov.courseFlow.dtos;

import com.liushukov.courseFlow.models.CourseTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CourseDto(
        @Size(min = 4, max = 60, message = "name for course should have at least 4 symbols")
        @NotBlank(message = "name for course is mandatory")
        String name,
        @Size(min = 10, max = 3000, message = "description should have at least 10 symbols")
        @NotBlank(message = "description is mandatory")
        String description,
        @NotNull(message = "typeEnum is mandatory")
        CourseTypeEnum type
) {
}
