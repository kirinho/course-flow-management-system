package com.liushukov.courseFlow.controllers;

import com.liushukov.courseFlow.dtos.LessonResponseDto;
import com.liushukov.courseFlow.models.Lesson;
import com.liushukov.courseFlow.services.LessonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping(path = "/lesson")
public class LessonController {
    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @GetMapping(path = "/course/{courseId}/lesson/{lessonId}/overview")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LessonResponseDto> lesson(
            @PathVariable(value = "courseId") Long courseId,
            @PathVariable(value = "lessonId") Long lessonId) {
        Optional<Lesson> lesson = lessonService.getLessonById(lessonId);
        if (lesson.isPresent() && lesson.get().getModule().getCourse().getId().equals(courseId)) {
            return ResponseEntity.status(HttpStatus.OK).body(lessonService.getLessonOverview(lesson.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
