package com.liushukov.courseFlow.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.Date;

public record AssignmentUpdateDto(
        @Size(max = 255, message = "title size should be up to 255 characters")
        String title,
        @Size(max = 255, message = "description size should be up to 255 characters")
        String description,
        @NotNull(message = "position is mandatory")
        @Positive(message = "position must be a positive number")
        Integer position,
        Date dueDate,
        @Positive(message = "max score must be a positive number")
        Integer maxScore
) {
}
