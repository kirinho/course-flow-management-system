package com.liushukov.courseFlow.controllers;

import com.liushukov.courseFlow.dtos.ModuleCreateDto;
import com.liushukov.courseFlow.dtos.ModuleResponseDto;
import com.liushukov.courseFlow.dtos.ModuleUpdateDto;
import com.liushukov.courseFlow.models.Course;
import com.liushukov.courseFlow.models.Module;
import com.liushukov.courseFlow.services.CourseService;
import com.liushukov.courseFlow.services.ModuleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/manager/modules")
public class ModuleManagerController {
    private final ModuleService moduleService;
    private final CourseService courseService;

    public ModuleManagerController(ModuleService moduleService, CourseService courseService) {
        this.moduleService = moduleService;
        this.courseService = courseService;
    }

    @GetMapping(path = "/{courseId}/all")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<ModuleResponseDto>> allForManager(
            @PathVariable(value = "courseId") Long courseId,
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize
    ) {
        Optional<Course> course = courseService.getCourseById(courseId);
        if (course.isPresent()) {
            List<ModuleResponseDto> modules = moduleService.getAllModulesByCourse(course.get(), pageNumber, pageSize);
            return ResponseEntity.status(HttpStatus.OK).body(modules);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping(path = "/{courseId}/create")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> createModule(
            @PathVariable(value = "courseId") Long courseId,
            @Valid @RequestBody ModuleCreateDto moduleCreateDto
    ) {
        Optional<Course> course = courseService.getCourseById(courseId);
        if (course.isPresent()) {
            moduleService.createModule(moduleCreateDto, course.get());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PatchMapping(path = "/{courseId}/update/{moduleId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> updateModule(
            @PathVariable(value = "courseId") Long courseId,
            @PathVariable(value = "moduleId") Long moduleId,
            @Valid @RequestBody ModuleUpdateDto moduleUpdateDto
    ) {
        Optional<Course> course = courseService.getCourseById(courseId);
        Optional<Module> module = moduleService.getModuleById(moduleId);
        if (course.isPresent() && module.isPresent()) {
            moduleService.updateModule(module.get(), moduleUpdateDto);
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping(path = "/delete/{moduleId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> deleteModule(@PathVariable(value = "moduleId") Long moduleId) {
        Optional<Module> module = moduleService.getModuleById(moduleId);
        if (module.isPresent()) {
            moduleService.deleteModule(module.get());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
