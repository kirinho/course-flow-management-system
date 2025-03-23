package com.liushukov.courseFlow.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.Date;

public record AssignmentCreateDto(
        @NotNull(message = "title is mandatory")
        @Size(max = 255, message = "title size should be up to 255 characters")
        String title,
        @NotNull(message = "description is mandatory")
        @Size(max = 255, message = "description size should be up to 255 characters")
        String description,
        @NotNull(message = "position is mandatory")
        @Positive(message = "position must be a positive number")
        Integer position,
        @NotNull(message = "due date is mandatory")
        Date dueDate,
        @NotNull(message = "max score is mandatory")
        @Positive(message = "max score must be a positive number")
        Integer maxScore
) {
}
