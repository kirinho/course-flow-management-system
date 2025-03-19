package com.liushukov.courseFlow.services;

import com.liushukov.courseFlow.dtos.GradeCreateDto;
import com.liushukov.courseFlow.dtos.GradeUpdateDto;
import com.liushukov.courseFlow.models.Grade;
import com.liushukov.courseFlow.models.Submission;
import com.liushukov.courseFlow.models.User;

import java.util.Optional;

public interface GradeService {
    Optional<Grade> getGradeById(long gradeId);

    void createGrade(GradeCreateDto gradeCreateDto, User manager, Submission submission);

    void updateGrade(Grade grade, GradeUpdateDto gradeUpdateDto);

    void deleteGrade(Grade grade);
}
