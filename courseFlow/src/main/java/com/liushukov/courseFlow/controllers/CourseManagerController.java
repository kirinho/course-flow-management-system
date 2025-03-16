package com.liushukov.courseFlow.controllers;

import com.liushukov.courseFlow.dtos.CourseDto;
import com.liushukov.courseFlow.dtos.CourseManagerResponseDto;
import com.liushukov.courseFlow.models.Course;
import com.liushukov.courseFlow.models.User;
import com.liushukov.courseFlow.services.CourseService;
import com.liushukov.courseFlow.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/manager/courses")
public class CourseManagerController {
    private final UserService userService;
    private final CourseService courseService;

    public CourseManagerController(UserService userService, CourseService courseService) {
        this.userService = userService;
        this.courseService = courseService;
    }

    @GetMapping(path = "/course/{courseId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<String> courseName(@PathVariable(value = "courseId") Long courseId) {
        Optional<Course> course = courseService.getCourseById(courseId);
        return course
                .map(value -> ResponseEntity.status(HttpStatus.OK).body(value.getName()))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping(path = "/all")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<CourseManagerResponseDto>> all(
            Authentication authentication,
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize
    ) {
        User user = userService.getUserFromAuthentication(authentication);
        List<CourseManagerResponseDto> courses = courseService.getCoursesByManager(user.getId(), pageNumber, pageSize);
        return ResponseEntity.status(HttpStatus.OK).body(courses);
    }

    @PostMapping(path = "/course/create", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> createCourse(
            Authentication authentication,
            @RequestPart("name") String courseName,
            @RequestPart("description") String courseDescription,
            @RequestPart("image") MultipartFile image) throws IOException
    {
        User user = userService.getUserFromAuthentication(authentication);
        CourseDto courseDto = new CourseDto(courseName, courseDescription);
        courseService.createCourse(user, courseDto, image);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping(path = "/course/update/{courseId}", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> updateCourse(
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
                courseService.updateCourse(course.get(), courseDto, image);
                return ResponseEntity.status(HttpStatus.OK).build();
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
