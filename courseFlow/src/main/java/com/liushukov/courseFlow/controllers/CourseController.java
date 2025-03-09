package com.liushukov.courseFlow.controllers;

import com.liushukov.courseFlow.dtos.CourseDto;
import com.liushukov.courseFlow.dtos.CoursePageResponseDto;
import com.liushukov.courseFlow.dtos.CourseResponseDto;
import com.liushukov.courseFlow.models.Course;
import com.liushukov.courseFlow.models.Enrollment;
import com.liushukov.courseFlow.models.SortingOrderEnum;
import com.liushukov.courseFlow.models.User;
import com.liushukov.courseFlow.services.CourseService;
import com.liushukov.courseFlow.services.EnrollmentService;
import com.liushukov.courseFlow.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
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
            @RequestParam(value = "pageSize", defaultValue = "12", required = false) int pageSize
    ) {
        List<CourseResponseDto> courses = courseService.getAllCourses(sortBy, orderBy, pageNumber, pageSize);
        return ResponseEntity.status(HttpStatus.OK).body(courses);
    }

    @GetMapping(path = "/all-manager")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<CourseResponseDto>> all(
            Authentication authentication,
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "12", required = false) Integer pageSize
    ) {
        User user = userService.getUserFromAuthentication(authentication);
        List<CourseResponseDto> courses = courseService.getCoursesByManager(user.getId(), pageNumber, pageSize);
        return ResponseEntity.status(HttpStatus.OK).body(courses);
    }

    @PostMapping(path = "/course/create", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Course> createCourse(
            Authentication authentication,
            @RequestPart("name") String courseName,
            @RequestPart("description") String courseDescription,
            @RequestPart("image") MultipartFile image) throws IOException
    {
        User user = userService.getUserFromAuthentication(authentication);
        CourseDto courseDto = new CourseDto(courseName, courseDescription);
        Course course = courseService.createCourse(user, courseDto, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(course);
    }

    @PatchMapping(path = "/course/update/{courseId}", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Course> updateCourse(
            Authentication authentication,
            @PathVariable(value = "courseId") Long courseId,
            @RequestPart(value = "name", required = false) String courseName,
            @RequestPart(value = "description", required = false) String courseDescription,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException

    {
        User user = userService.getUserFromAuthentication(authentication);
        Optional<Course> course = courseService.getCourseById(courseId);
        if (course.isPresent()) {
            if (course.get().getUser().equals(user)) {
                CourseDto courseDto = new CourseDto(courseName, courseDescription);
                return ResponseEntity.status(HttpStatus.OK)
                        .body(courseService.updateCourse(course.get(), courseDto, image));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping(path = "/course/delete/{courseId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> deleteCourse(Authentication authentication,
                                             @PathVariable(value = "courseId") Long courseId
    ) {
        User user = userService.getUserFromAuthentication(authentication);
        Optional<Course> course = courseService.getCourseById(courseId);
        if (course.isPresent()) {
            if (course.get().getUser().equals(user)) {
                courseService.deleteCourse(course.get());
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
