package com.liushukov.courseFlow.dtos;

import com.liushukov.courseFlow.models.User;

public record CourseManagerResponseDto(
        Long id,
        String name,
        String description,
        String enrollmentCode,
        String imageBase64,
        User user
) {
}
