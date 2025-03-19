package com.liushukov.courseFlow.dtos;

import java.util.Date;
import java.util.List;

public record SubmissionResponseDto(
        Long id,
        Date submittedAt,
        String textSubmission,
        List<AttachmentResponseDto> attachments,
        GradeResponseDto grade
) {
}
