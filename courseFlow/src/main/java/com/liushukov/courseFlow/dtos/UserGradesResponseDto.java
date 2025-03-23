package com.liushukov.courseFlow.dtos;

public record UserGradesResponseDto(
        Long studentId,
        String fullName,
        Integer totalScore,
        Integer maxTotalScore
) {
}
