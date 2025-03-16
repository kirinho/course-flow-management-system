package com.liushukov.courseFlow.dtos;

import java.util.List;

public record CourseOverviewDto(
        Long id,
        String name,
        String description,
        String image,
        List<ModulePageResponseDto> modules
) {
}
