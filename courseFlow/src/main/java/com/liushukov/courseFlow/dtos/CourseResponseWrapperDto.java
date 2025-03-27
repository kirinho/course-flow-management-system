package com.liushukov.courseFlow.dtos;

import java.util.List;

public record CourseResponseWrapperDto(
        Integer totalPages,
        List<CourseResponseDto> courses
) {
}
