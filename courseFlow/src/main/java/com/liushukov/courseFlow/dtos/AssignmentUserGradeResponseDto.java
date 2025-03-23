package com.liushukov.courseFlow.dtos;

import java.util.Date;

public record AssignmentUserGradeResponseDto(
        Long id,
        String title,
        String description,
        Date dueDate,
        Integer maxScore,
        Integer currentScore
) {
}
