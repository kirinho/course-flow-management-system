package com.liushukov.courseFlow.dtos;

import java.util.List;

public record ModulePageResponseDto(
        Long id,
        String name,
        String description,
        List<LessonAssignmentPageResponseDto> lessonAssignments
) {
}
