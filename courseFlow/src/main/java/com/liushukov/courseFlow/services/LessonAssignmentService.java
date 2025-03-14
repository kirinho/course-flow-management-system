package com.liushukov.courseFlow.services;

import com.liushukov.courseFlow.dtos.AttachmentResponseDto;
import com.liushukov.courseFlow.dtos.LessonAssignmentResponseDto;
import com.liushukov.courseFlow.models.Assignment;
import com.liushukov.courseFlow.models.BaseLessonAssignment;
import com.liushukov.courseFlow.models.Lesson;
import com.liushukov.courseFlow.models.Module;
import com.liushukov.courseFlow.repositories.BaseLessonAssignmentRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LessonAssignmentService {
    private final BaseLessonAssignmentRepository repository;

    public LessonAssignmentService(BaseLessonAssignmentRepository repository) {
        this.repository = repository;
    }

    public List<LessonAssignmentResponseDto> getAllByModule(Module module, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<BaseLessonAssignment> assignments = repository.findAllByModuleId(module.getId(), pageable).getContent();
        return assignments.stream().map(this::mapToDto).toList();
    }

    private LessonAssignmentResponseDto mapToDto(BaseLessonAssignment assignment) {
        List<AttachmentResponseDto> attachments = assignment.getAttachments().stream()
                .map(att -> new AttachmentResponseDto(att.getId(), att.getFileName(), att.getFileType().name()))
                .toList();

        if (assignment instanceof Lesson lesson) {
            return new LessonAssignmentResponseDto(
                    lesson.getId(),
                    lesson.getTitle(),
                    lesson.getDescription(),
                    "LESSON",
                    lesson.getPosition(),
                    attachments,
                    lesson.getContent(),
                    null,
                    null
            );
        } else if (assignment instanceof Assignment assign) {
            return new LessonAssignmentResponseDto(
                    assign.getId(),
                    assign.getTitle(),
                    assign.getDescription(),
                    "ASSIGNMENT",
                    assign.getPosition(),
                    attachments,
                    null,
                    assign.getDueDate(),
                    assign.getMaxScore()
            );
        }

        throw new IllegalArgumentException("Unknown assignment type");
    }
}
