package com.liushukov.courseFlow.controllers;

import com.liushukov.courseFlow.dtos.LessonCreateDto;
import com.liushukov.courseFlow.dtos.LessonUpdateDto;
import com.liushukov.courseFlow.models.Lesson;
import com.liushukov.courseFlow.models.Module;
import com.liushukov.courseFlow.services.LessonService;
import com.liushukov.courseFlow.services.ModuleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/manager/lessons")
public class LessonManagerController {
    private final ModuleService moduleService;
    private final LessonService lessonService;

    public LessonManagerController(ModuleService moduleService, LessonService lessonService) {
        this.moduleService = moduleService;
        this.lessonService = lessonService;
    }

    @GetMapping(path = "/lesson/{lessonId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Lesson> lesson(@PathVariable(value = "lessonId") Long lessonId) {
        Optional<Lesson> lesson = lessonService.getLessonById(lessonId);
        return (lesson.isPresent())
                ? ResponseEntity.status(HttpStatus.OK).body(lesson.get())
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping(path = "/{moduleId}/lesson/create")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> create(
            @PathVariable Long moduleId,
            @RequestPart("item") @Valid LessonCreateDto lessonDto,
            @RequestPart(value = "attachments", required = false) MultipartFile[] attachments
    ) {
        Optional<Module> module = moduleService.getModuleById(moduleId);
        if (module.isPresent()) {
            lessonService.createLesson(lessonDto, module.get(), attachments);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PatchMapping(path = "/{moduleId}/lesson/update/{lessonId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> update(
            @PathVariable(value = "moduleId") Long moduleId,
            @PathVariable(value = "lessonId") Long lessonId,
            @RequestPart(value = "item") LessonUpdateDto lessonUpdateDto,
            @RequestPart(value = "attachments", required = false) MultipartFile[] attachments
    ) {
        Optional<Module> module = moduleService.getModuleById(moduleId);
        Optional<Lesson> lesson = lessonService.getLessonById(lessonId);
        if (module.isPresent() && lesson.isPresent()) {
            lessonService.updateLesson(lesson.get(), lessonUpdateDto, attachments);
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping(path = "/delete/{lessonId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> delete(@PathVariable(value = "lessonId") Long lessonId) {
        Optional<Lesson> lesson = lessonService.getLessonById(lessonId);
        if (lesson.isPresent()) {
            lessonService.deleteLesson(lesson.get());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
