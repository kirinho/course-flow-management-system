package com.liushukov.courseFlow.dtos;

import java.time.Instant;
import java.util.Date;
import java.util.List;

public record LessonAssignmentResponseDto(
        Long id,
        String title,
        String description,
        String type,
        Integer position,
        List<AttachmentResponseDto> attachments,
        String content,
        Date dueDate,
        Integer maxScore
) {
}
