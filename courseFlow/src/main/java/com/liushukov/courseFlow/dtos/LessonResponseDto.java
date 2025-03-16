package com.liushukov.courseFlow.dtos;

import java.util.List;

public record LessonResponseDto(
        Long id,
        String title,
        String description,
        String content,
        List<AttachmentResponseDto> attachments
) {
}
