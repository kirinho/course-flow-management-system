package com.liushukov.courseFlow.dtos;

import jakarta.validation.constraints.Size;

public record UpdateUserDto(
        @Size(min = 2, max = 100, message = "Full Name should have at least 2 symbols")
        String fullName,
        @Size(min = 8, max = 100, message = "Password should contain at least 8 symbols")
        String password
) {
}
