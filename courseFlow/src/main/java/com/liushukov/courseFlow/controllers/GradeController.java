package com.liushukov.courseFlow.controllers;

import com.liushukov.courseFlow.dtos.AssignmentUserGradeResponseDto;
import com.liushukov.courseFlow.models.Course;
import com.liushukov.courseFlow.models.User;
import com.liushukov.courseFlow.services.CourseService;
import com.liushukov.courseFlow.services.GradeService;
import com.liushukov.courseFlow.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/grades")
public class GradeController {
    private final UserService userService;
    private final CourseService courseService;
    private final GradeService gradeService;

    public GradeController(UserService userService, CourseService courseService, GradeService gradeService) {
        this.userService = userService;
        this.courseService = courseService;
        this.gradeService = gradeService;
    }

    @GetMapping(path = "/all")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<AssignmentUserGradeResponseDto>> grades(
            Authentication authentication,
            @RequestParam(value = "courseId") Long courseId
    ) {
        User user = userService.getUserFromAuthentication(authentication);
        Optional<Course> course = courseService.getCourseById(courseId);

        return course
                .map(value -> ResponseEntity.status(HttpStatus.OK)
                        .body(gradeService.getUserAssignmentsAndGrades(courseId, user.getId())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
