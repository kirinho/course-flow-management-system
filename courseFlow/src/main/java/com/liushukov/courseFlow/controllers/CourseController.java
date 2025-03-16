package com.liushukov.courseFlow.controllers;

import com.liushukov.courseFlow.dtos.CourseOverviewDto;
import com.liushukov.courseFlow.dtos.CoursePageResponseDto;
import com.liushukov.courseFlow.dtos.CourseResponseDto;
import com.liushukov.courseFlow.models.Course;
import com.liushukov.courseFlow.models.Enrollment;
import com.liushukov.courseFlow.models.User;
import com.liushukov.courseFlow.services.CourseService;
import com.liushukov.courseFlow.services.EnrollmentService;
import com.liushukov.courseFlow.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/courses")
public class CourseController {
    private final UserService userService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;

    public CourseController(UserService userService, CourseService courseService, EnrollmentService enrollmentService) {
        this.userService = userService;
        this.courseService = courseService;
        this.enrollmentService = enrollmentService;
    }

    @GetMapping(path = "/course/{courseId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CoursePageResponseDto> course(Authentication authentication,
                                                        @PathVariable(value = "courseId") Long courseId) {
        User user = userService.getUserFromAuthentication(authentication);
        Optional<Course> course = courseService.getCourseById(courseId);
        if (user.isEnabled() && course.isPresent()) {
            Optional<Enrollment> enrollment = enrollmentService.getEnrollmentByUserAndCourse(user, course.get());
            if (enrollment.isPresent() || course.get().getUser().equals(user)) {
                CoursePageResponseDto coursePageResponseDto =
                        courseService.toCoursePageResponseDto(course.get(), user, true);
                return ResponseEntity.status(HttpStatus.OK).body(coursePageResponseDto);
            } else {
                CoursePageResponseDto coursePageResponseDto =
                        courseService.toCoursePageResponseDto(course.get(), user, false);
                return ResponseEntity.status(HttpStatus.OK).body(coursePageResponseDto);
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping(path = "/all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CourseResponseDto>> allUsers(
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "orderBy", defaultValue = "asc") String orderBy,
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "20", required = false) int pageSize
    ) {
        List<CourseResponseDto> courses = courseService.getAllCourses(sortBy, orderBy, pageNumber, pageSize);
        return ResponseEntity.status(HttpStatus.OK).body(courses);
    }

    @GetMapping(path = "/{courseId}/overview")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CourseOverviewDto> courseOverview(
            @PathVariable(value = "courseId") Long courseId,
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "20", required = false) int pageSize
    ) {
        Optional<Course> course = courseService.getCourseById(courseId);
        if (course.isPresent()) {
            CourseOverviewDto courseOverviewDto = courseService.getAllModulesWithLessonsAndAssignments(
                    course.get(),
                    pageNumber,
                    pageSize
            );
            return ResponseEntity.status(HttpStatus.OK).body(courseOverviewDto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
