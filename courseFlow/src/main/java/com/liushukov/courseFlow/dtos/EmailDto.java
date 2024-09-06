package com.liushukov.courseFlow.dtos;

import jakarta.validation.constraints.Email;

public record EmailDto(
        String receiver,
        String message
) {
}
