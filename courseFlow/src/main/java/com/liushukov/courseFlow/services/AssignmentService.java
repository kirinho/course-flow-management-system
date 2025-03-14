package com.liushukov.courseFlow.services;

import com.liushukov.courseFlow.dtos.AssignmentCreateDto;
import com.liushukov.courseFlow.dtos.AssignmentUpdateDto;
import com.liushukov.courseFlow.models.Assignment;
import com.liushukov.courseFlow.models.Module;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface AssignmentService {
    Optional<Assignment> getAssignmentById(long id);

    void createAssignment(AssignmentCreateDto assignmentCreateDto, Module module, MultipartFile[] files);

    void updateAssignment(Assignment assignment, AssignmentUpdateDto assignmentUpdateDto, MultipartFile[] files);

    void deleteAssignment(Assignment assignment);
}
