package com.liushukov.courseFlow.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public record CourseDto(
        @Size(min = 3, max = 100, message = "name for course should be from 3 to 100 characters")
        String name,
        @Size(min = 5, message = "description for course should be from 5 characters")
        String description
) {
}
