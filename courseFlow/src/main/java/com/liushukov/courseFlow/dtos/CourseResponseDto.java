package com.liushukov.courseFlow.dtos;

import com.liushukov.courseFlow.models.User;

import java.time.Instant;

public record CourseResponseDto(
        Long id,
        String name,
        String description,
        String imageBase64,
        User user
) {
}
