package com.liushukov.courseFlow.dtos;

import java.util.Date;
import java.util.List;

public record AssignmentResponseDto(
        Long id,
        String title,
        String description,
        Date dueDate,
        Integer maxScore,
        List<AttachmentResponseDto> attachments,
        SubmissionResponseDto submission
) {
}
