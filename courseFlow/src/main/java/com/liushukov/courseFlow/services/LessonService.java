package com.liushukov.courseFlow.services;

import com.liushukov.courseFlow.dtos.LessonCreateDto;
import com.liushukov.courseFlow.dtos.LessonUpdateDto;
import com.liushukov.courseFlow.models.Lesson;
import com.liushukov.courseFlow.models.Module;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface LessonService {
    Optional<Lesson> getLessonById(long id);

    void createLesson(LessonCreateDto lessonCreateDto, Module module, MultipartFile[] files);

    void updateLesson(Lesson lesson, LessonUpdateDto lessonUpdateDto, MultipartFile[] files);

    void deleteLesson(Lesson lesson);
}
