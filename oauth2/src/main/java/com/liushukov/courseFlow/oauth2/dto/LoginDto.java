package com.liushukov.courseFlow.oauth2.dto;

public record LoginDto(
        String name,
        String surname,
        String email
) {
}
