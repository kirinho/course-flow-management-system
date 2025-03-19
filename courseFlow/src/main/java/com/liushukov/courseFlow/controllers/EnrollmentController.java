package com.liushukov.courseFlow.controllers;

import com.liushukov.courseFlow.dtos.EnrollDto;
import com.liushukov.courseFlow.models.Course;
import com.liushukov.courseFlow.models.Enrollment;
import com.liushukov.courseFlow.models.User;
import com.liushukov.courseFlow.services.CourseService;
import com.liushukov.courseFlow.services.EnrollmentService;
import com.liushukov.courseFlow.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(path = "/enroll")
public class EnrollmentController {
    private final UserService userService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;

    public EnrollmentController(UserService userService, CourseService courseService, EnrollmentService enrollmentService) {
        this.userService = userService;
        this.courseService = courseService;
        this.enrollmentService = enrollmentService;
    }

    @GetMapping(path = "/info/{courseId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> enrolled(Authentication authentication, @PathVariable(value = "courseId") Long courseId) {
        User user = userService.getUserFromAuthentication(authentication);
        Optional<Course> course = courseService.getCourseById(courseId);
        if (user.isEnabled() && course.isPresent()) {
            Optional<Enrollment> enrollment = enrollmentService.getEnrollmentByUserAndCourse(user, course.get());
            if (enrollment.isPresent() || course.get().getUser().equals(user)) {
                return ResponseEntity.status(HttpStatus.OK).build();
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> enrolling(Authentication authentication, @Valid @RequestBody EnrollDto enrollDto) {
        User user = userService.getUserFromAuthentication(authentication);
        Optional<Course> course = courseService.getCourseById(enrollDto.courseId());
        if (user.isEnabled() && course.isPresent()) {
            if (course.get().getEnrollmentCode().equals(enrollDto.enrollmentCode())) {
                enrollmentService.createEnrollment(user, course.get());
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping(path = "/cancel/{courseId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> cancel(Authentication authentication, @PathVariable(value = "courseId") Long courseId) {
        User user = userService.getUserFromAuthentication(authentication);
        Optional<Course> course = courseService.getCourseById(courseId);
        if (user.isEnabled() && course.isPresent()) {
            Optional<Enrollment> enrollment = enrollmentService.getEnrollmentByUserAndCourse(user, course.get());
            if (enrollment.isPresent()) {
                enrollmentService.deleteEnrollment(enrollment.get());
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
