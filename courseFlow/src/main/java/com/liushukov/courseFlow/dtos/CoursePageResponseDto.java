package com.liushukov.courseFlow.dtos;

public record CoursePageResponseDto(
        Long id,
        String name,
        String description,
        String imageBase64,
        Boolean enrolled,
        Long userId
) {
}
