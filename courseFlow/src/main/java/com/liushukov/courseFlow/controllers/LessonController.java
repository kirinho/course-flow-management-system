package com.liushukov.courseFlow.controllers;

import com.liushukov.courseFlow.dtos.LessonResponseDto;
import com.liushukov.courseFlow.models.Lesson;
import com.liushukov.courseFlow.services.LessonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping(path = "/{lessonId}/overview")
    public ResponseEntity<LessonResponseDto> lesson(@PathVariable(value = "lessonId") Long lessonId) {
        Optional<Lesson> lesson = lessonService.getLessonById(lessonId);
        return lesson
                .map(value -> ResponseEntity.status(HttpStatus.OK).body(lessonService.getLessonOverview(value)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
