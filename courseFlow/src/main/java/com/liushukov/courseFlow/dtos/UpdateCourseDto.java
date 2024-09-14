package com.liushukov.courseFlow.dtos;

import com.liushukov.courseFlow.models.CourseTypeEnum;
import jakarta.validation.constraints.Size;

public record UpdateCourseDto(
        @Size(min = 4, max = 60, message = "name for course should have at least 4 symbols")
        String name,
        @Size(min = 10, max = 3000, message = "description should have at least 10 symbols")
        String description,
        CourseTypeEnum type
) {
}
