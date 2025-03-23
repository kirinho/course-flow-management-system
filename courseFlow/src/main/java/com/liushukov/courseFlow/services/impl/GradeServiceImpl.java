package com.liushukov.courseFlow.services.impl;

import com.liushukov.courseFlow.dtos.*;
import com.liushukov.courseFlow.models.Assignment;
import com.liushukov.courseFlow.models.Grade;
import com.liushukov.courseFlow.models.Submission;
import com.liushukov.courseFlow.models.User;
import com.liushukov.courseFlow.repositories.BaseLessonAssignmentRepository;
import com.liushukov.courseFlow.repositories.EnrollmentRepository;
import com.liushukov.courseFlow.repositories.GradeRepository;
import com.liushukov.courseFlow.services.GradeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GradeServiceImpl implements GradeService {
    private final GradeRepository gradeRepository;
    private final BaseLessonAssignmentRepository repository;
    private final EnrollmentRepository enrollmentRepository;

    public GradeServiceImpl(GradeRepository gradeRepository, BaseLessonAssignmentRepository repository, EnrollmentRepository enrollmentRepository) {
        this.gradeRepository = gradeRepository;
        this.repository = repository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    public Optional<Grade> getGradeById(long gradeId) {
        return gradeRepository.findGradeById(gradeId);
    }

    @Override
    public List<AssignmentUserGradeResponseDto> getUserAssignmentsAndGrades(long courseId, long studentId) {
        List<Assignment> assignments = repository.findAssignmentsByCourseAndStudent(courseId, studentId);
        return assignments.stream().map(assignment -> {
            Submission submission = assignment.getSubmissions().stream()
                    .filter(s -> s.getStudent().getId().equals(studentId))
                    .findFirst()
                    .orElse(null);
            Integer currentScore = (submission != null && submission.getGrade() != null)
                    ? submission.getGrade().getScore()
                    : null;
            return new AssignmentUserGradeResponseDto(
                    assignment.getId(),
                    assignment.getTitle(),
                    assignment.getDescription(),
                    assignment.getDueDate(),
                    assignment.getMaxScore(),
                    currentScore
            );
        }).toList();
    }

    @Override
    public List<UserGradesResponseDto> getStudentsWithGradesByCourse(long courseId) {
        return enrollmentRepository.findStudentTotalGradesByCourse(courseId)
                .stream()
                .map(value -> new UserGradesResponseDto(
                        value.getStudentId(),
                        value.getFullName(),
                        value.getTotalScore(),
                        value.getMaxTotalScore()
                )).toList();
    }

    @Override
    public void createGrade(GradeCreateDto gradeCreateDto, User manager, Submission submission) {
        Grade grade = new Grade(gradeCreateDto.score(), gradeCreateDto.feedback(), manager, submission);
        gradeRepository.save(grade);
    }

    @Override
    public void updateGrade(Grade grade, GradeUpdateDto gradeUpdateDto) {
        if (gradeUpdateDto.score() != null) {
            grade.setScore(gradeUpdateDto.score());
        }
        if (gradeUpdateDto.feedback() != null) {
            grade.setFeedback(gradeUpdateDto.feedback());
        }
        gradeRepository.save(grade);
    }

    @Override
    public void deleteGrade(Grade grade) {
        grade.getSubmission().setGrade(null);
        gradeRepository.delete(grade);
    }
}
