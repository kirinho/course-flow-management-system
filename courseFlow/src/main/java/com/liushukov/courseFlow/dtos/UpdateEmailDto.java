package com.liushukov.courseFlow.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateEmailDto(
        @Email(message = "Email should be valid")
        @NotBlank(message = "Email is mandatory")
        String email
) {
}
