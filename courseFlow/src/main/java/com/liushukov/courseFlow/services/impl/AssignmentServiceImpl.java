package com.liushukov.courseFlow.services.impl;

import com.liushukov.courseFlow.dtos.AssignmentCreateDto;
import com.liushukov.courseFlow.dtos.AssignmentUpdateDto;
import com.liushukov.courseFlow.models.*;
import com.liushukov.courseFlow.models.Module;
import com.liushukov.courseFlow.repositories.AttachmentRepository;
import com.liushukov.courseFlow.repositories.BaseLessonAssignmentRepository;
import com.liushukov.courseFlow.services.AssignmentService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.liushukov.courseFlow.models.AttachmentExtension.*;
import static com.liushukov.courseFlow.models.AttachmentExtension.UNSUPPORTED;

@Service
public class AssignmentServiceImpl implements AssignmentService {
    private final BaseLessonAssignmentRepository repository;
    private final AttachmentRepository attachmentRepository;

    public AssignmentServiceImpl(BaseLessonAssignmentRepository repository, AttachmentRepository attachmentRepository) {
        this.repository = repository;
        this.attachmentRepository = attachmentRepository;
    }

    @Override
    public Optional<Assignment> getAssignmentById(long id) {
        return repository.findAssignmentById(id);
    }

    @Transactional
    @Override
    public void createAssignment(AssignmentCreateDto assignmentCreateDto, Module module, MultipartFile[] files) {
        Assignment assignment = new Assignment(
                assignmentCreateDto.title(),
                assignmentCreateDto.description(),
                assignmentCreateDto.position(),
                assignmentCreateDto.dueDate(),
                assignmentCreateDto.maxScore(),
                module
        );
        Assignment savedAssignment = repository.save(assignment);
        if (files != null && files.length > 0) {
            saveAttachments(savedAssignment, files);
        }
    }

    @Transactional
    @Override
    public void updateAssignment(Assignment assignment, AssignmentUpdateDto assignmentUpdateDto, MultipartFile[] files) {
        if (assignmentUpdateDto.title() != null) {
            assignment.setTitle(assignmentUpdateDto.title());
        }
        if (assignmentUpdateDto.description() != null) {
            assignment.setDescription(assignmentUpdateDto.description());
        }
        if (assignmentUpdateDto.dueDate() != null) {
            assignment.setDueDate(assignmentUpdateDto.dueDate());
        }
        if (assignmentUpdateDto.maxScore() != null) {
            assignment.setMaxScore(assignmentUpdateDto.maxScore());
        }
        assignment.setPosition(assignmentUpdateDto.position());
        Assignment updatedAssignment = repository.save(assignment);
        if (files != null && files.length > 0) {
            attachmentRepository.deleteByLessonAssignment(updatedAssignment);
            saveAttachments(updatedAssignment, files);
        }
    }

    @Override
    public void deleteAssignment(Assignment assignment) {
        repository.delete(assignment);
    }

    private void saveAttachments(Assignment assignment, MultipartFile[] files) {
        List<Attachment> attachments = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                attachments.add(new Attachment(
                        file.getOriginalFilename(),
                        getFileExtension(file.getOriginalFilename()),
                        file.getBytes(),
                        assignment
                ));
            } catch (IOException e) {
                throw new RuntimeException("Error processing file: " + file.getOriginalFilename(), e);
            }
        }
        attachmentRepository.saveAll(attachments);
    }

    private AttachmentExtension getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            throw new IllegalArgumentException("Invalid file name: " + fileName);
        }
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return switch (extension) {
            case "docx" -> DOCX;
            case "doc" -> DOC;
            case "pdf" -> PDF;
            case "xlsx" -> XSLX;
            case "xls" -> XSL;
            default -> UNSUPPORTED;
        };
    }
}
