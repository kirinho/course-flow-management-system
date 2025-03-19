package com.liushukov.courseFlow.dtos;

import jakarta.validation.constraints.NotNull;

public record GradeCreateDto(
        @NotNull(message = "score is mandatory")
        Integer score,
        String feedback
) {
}
