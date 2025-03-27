package com.liushukov.courseFlow.dtos;

public record CourseResponseDto(
        Long id,
        String name,
        String description,
        String imageBase64,
        String authorName,
        String authorEmail
) {
}
