package com.liushukov.courseFlow.services.impl;

import com.liushukov.courseFlow.dtos.GradeCreateDto;
import com.liushukov.courseFlow.dtos.GradeUpdateDto;
import com.liushukov.courseFlow.models.Grade;
import com.liushukov.courseFlow.models.Submission;
import com.liushukov.courseFlow.models.User;
import com.liushukov.courseFlow.repositories.GradeRepository;
import com.liushukov.courseFlow.services.GradeService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GradeServiceImpl implements GradeService {
    private final GradeRepository gradeRepository;

    public GradeServiceImpl(GradeRepository gradeRepository) {
        this.gradeRepository = gradeRepository;
    }

    @Override
    public Optional<Grade> getGradeById(long gradeId) {
        return gradeRepository.findGradeById(gradeId);
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
