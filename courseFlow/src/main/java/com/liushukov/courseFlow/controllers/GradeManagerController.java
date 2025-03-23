package com.liushukov.courseFlow.controllers;

import com.liushukov.courseFlow.dtos.GradeCreateDto;
import com.liushukov.courseFlow.dtos.GradeUpdateDto;
import com.liushukov.courseFlow.dtos.UserGradesResponseDto;
import com.liushukov.courseFlow.models.Course;
import com.liushukov.courseFlow.models.Grade;
import com.liushukov.courseFlow.models.Submission;
import com.liushukov.courseFlow.models.User;
import com.liushukov.courseFlow.services.CourseService;
import com.liushukov.courseFlow.services.GradeService;
import com.liushukov.courseFlow.services.SubmissionService;
import com.liushukov.courseFlow.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/manager/grade")
public class GradeManagerController {
    private final UserService userService;
    private final CourseService courseService;
    private final SubmissionService submissionService;
    private final GradeService gradeService;

    public GradeManagerController(UserService userService, CourseService courseService, SubmissionService submissionService, GradeService gradeService) {
        this.userService = userService;
        this.courseService = courseService;
        this.submissionService = submissionService;
        this.gradeService = gradeService;
    }

    @GetMapping(path = "/all-students")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<UserGradesResponseDto>> studentsGrades(
            Authentication authentication,
            @RequestParam(value = "courseId") Long courseId
    ) {
        User user = userService.getUserFromAuthentication(authentication);
        Optional<Course> course = courseService.getCourseById(courseId);
        if (course.isPresent() && course.get().getUser().equals(user)) {
            return ResponseEntity.status(HttpStatus.OK).body(gradeService.getStudentsWithGradesByCourse(courseId));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping(path = "/{submissionId}/create")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> create(
            Authentication authentication,
            @PathVariable(value = "submissionId") Long submissionId,
            @Valid @RequestBody GradeCreateDto gradeCreateDto
    ) {
        User manager = userService.getUserFromAuthentication(authentication);
        Optional<Submission> submission = submissionService.getSubmissionById(submissionId);
        if (manager.isEnabled() && submission.isPresent()) {
            if (submission.get().getAssignment().getMaxScore() >= gradeCreateDto.score() && gradeCreateDto.score() > 0) {
                gradeService.createGrade(gradeCreateDto, manager, submission.get());
                return ResponseEntity.status(HttpStatus.CREATED).build();
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PatchMapping(path = "/{gradeId}/update")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> update(
            @PathVariable(value = "gradeId") Long gradeId,
            @Valid @RequestBody GradeUpdateDto gradeUpdateDto
    ) {
        Optional<Grade> grade = gradeService.getGradeById(gradeId);
        if (grade.isPresent()) {
            if (gradeUpdateDto.score() != null && gradeUpdateDto.score() > 0
                    && grade.get().getSubmission().getAssignment().getMaxScore() >= gradeUpdateDto.score()) {
                gradeService.updateGrade(grade.get(), gradeUpdateDto);
                return ResponseEntity.status(HttpStatus.OK).build();
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping(path = "/{gradeId}/delete")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> delete(@PathVariable(value = "gradeId") Long gradeId) {
        Optional<Grade> grade = gradeService.getGradeById(gradeId);
        if (grade.isPresent()) {
            gradeService.deleteGrade(grade.get());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
