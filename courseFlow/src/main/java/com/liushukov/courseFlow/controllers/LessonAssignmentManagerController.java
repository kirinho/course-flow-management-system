package com.liushukov.courseFlow.controllers;

import com.liushukov.courseFlow.dtos.LessonAssignmentResponseDto;
import com.liushukov.courseFlow.models.BaseLessonAssignment;
import com.liushukov.courseFlow.models.Module;
import com.liushukov.courseFlow.services.LessonAssignmentService;
import com.liushukov.courseFlow.services.ModuleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/manager/modules/overview")
public class LessonAssignmentManagerController {
    private final ModuleService moduleService;
    private final LessonAssignmentService service;

    public LessonAssignmentManagerController(ModuleService moduleService, LessonAssignmentService service) {
        this.moduleService = moduleService;
        this.service = service;
    }

    @GetMapping(path = "/{moduleId}/all")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<LessonAssignmentResponseDto>> all(@PathVariable Long moduleId) {
        Optional<Module> module = moduleService.getModuleById(moduleId);
        return module.map(m -> ResponseEntity.ok(service.getAllByModule(m, 0, 10)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
