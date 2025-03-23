package com.liushukov.courseFlow.controllers;

import com.liushukov.courseFlow.dtos.AssignmentCreateDto;
import com.liushukov.courseFlow.dtos.AssignmentUpdateDto;
import com.liushukov.courseFlow.models.Assignment;
import com.liushukov.courseFlow.models.Module;
import com.liushukov.courseFlow.services.AssignmentService;
import com.liushukov.courseFlow.services.ModuleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping(path = "/manager/assignments")
public class AssignmentManagerController {
    private final ModuleService moduleService;
    private final AssignmentService assignmentService;

    public AssignmentManagerController(ModuleService moduleService, AssignmentService assignmentService) {
        this.moduleService = moduleService;
        this.assignmentService = assignmentService;
    }

    @GetMapping(path = "/assignment/{assignmentId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Assignment> assignment(@PathVariable(value = "assignmentId") Long assignmentId) {
        Optional<Assignment> assignment = assignmentService.getAssignmentById(assignmentId);
        return (assignment.isPresent())
                ? ResponseEntity.status(HttpStatus.OK).body(assignment.get())
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping(path = "/{moduleId}/assignment/create")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> create(
            @PathVariable(value = "moduleId") Long moduleId,
            @RequestPart(value = "item") @Valid AssignmentCreateDto assignmentCreateDto,
            @RequestPart(value = "attachments", required = false) MultipartFile[] attachments
    ) {
        Optional<Module> module = moduleService.getModuleById(moduleId);
        if (module.isPresent()) {
            assignmentService.createAssignment(assignmentCreateDto, module.get(), attachments);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PatchMapping(path = "/{moduleId}/assignment/update/{assignmentId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> update(
            @PathVariable(value = "moduleId") Long moduleId,
            @PathVariable(value = "assignmentId") Long assignmentId,
            @RequestPart(value = "item") @Valid AssignmentUpdateDto assignmentUpdateDto,
            @RequestPart(value = "attachments", required = false) MultipartFile[] attachments
    ) {
        Optional<Module> module = moduleService.getModuleById(moduleId);
        Optional<Assignment> assignment = assignmentService.getAssignmentById(assignmentId);
        if (module.isPresent() && assignment.isPresent()) {
            assignmentService.updateAssignment(assignment.get(), assignmentUpdateDto, attachments);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping(path = "/delete/{assignmentId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> delete(@PathVariable(value = "assignmentId") Long assignmentId) {
        Optional<Assignment> assignment = assignmentService.getAssignmentById(assignmentId);
        if (assignment.isPresent()) {
            assignmentService.deleteAssignment(assignment.get());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
