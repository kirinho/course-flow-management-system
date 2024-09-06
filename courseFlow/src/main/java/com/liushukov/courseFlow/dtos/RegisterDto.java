package com.liushukov.courseFlow.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterDto(
        @Size(min = 2, max = 100, message = "Name should have at least 2 symbols")
        @NotBlank(message = "Name is mandatory")
        String name,
        @Size(min = 2, max = 100, message = "Surname should have at least 2 symbols")
        @NotBlank(message = "Surname is mandatory")
        String surname,
        @Email(message = "Email should be valid")
        @NotBlank(message = "Email is mandatory")
        String email,
        @Size(min = 8, max = 100, message = "Password should contain at least 8 symbols")
        @NotBlank(message = "Password is mandatory")
        String password
) {
}
