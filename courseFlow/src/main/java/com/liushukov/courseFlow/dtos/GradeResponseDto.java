package com.liushukov.courseFlow.dtos;

public record GradeResponseDto(
        Long id,
        Integer score,
        String feedback,
        String managerFullName
) {
}
