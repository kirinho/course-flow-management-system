package com.liushukov.courseFlow.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EnrollDto(
        @NotNull(message = "code is mandatory")
        @Size(max = 10, message = "length of enrollment code should be up to 10")
        String enrollmentCode,
        @NotNull(message = "courseId is mandatory")
        Long courseId
) {
}
