package com.liushukov.courseFlow.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginOauth2Dto(
        @Size(min = 2, max = 100, message = "Name should have at least 2 symbols")
        String name,
        @Size(min = 2, max = 100, message = "Surname should have at least 2 symbols")
        String surname,
        @Email(message = "Email should be valid")
        @NotBlank(message = "Email is mandatory")
        String email
) {
}
