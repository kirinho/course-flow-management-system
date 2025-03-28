package com.liushukov.courseFlow.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.liushukov.courseFlow.dtos.AssignmentUserGradeResponseDto;
import com.liushukov.courseFlow.dtos.GradeCreateDto;
import com.liushukov.courseFlow.dtos.GradeUpdateDto;
import com.liushukov.courseFlow.dtos.UserGradesResponseDto;
import com.liushukov.courseFlow.models.Grade;
import com.liushukov.courseFlow.models.Submission;
import com.liushukov.courseFlow.models.User;

import java.util.List;
import java.util.Optional;

public interface GradeService {
    Optional<Grade> getGradeById(long gradeId);

    List<AssignmentUserGradeResponseDto> getUserAssignmentsAndGrades(long courseId, long studentId);

    List<UserGradesResponseDto> getStudentsWithGradesByCourse(long courseId);

    void createGrade(GradeCreateDto gradeCreateDto, User manager, Submission submission) throws JsonProcessingException;

    void updateGrade(Grade grade, GradeUpdateDto gradeUpdateDto) throws JsonProcessingException;

    void deleteGrade(Grade grade);
}
