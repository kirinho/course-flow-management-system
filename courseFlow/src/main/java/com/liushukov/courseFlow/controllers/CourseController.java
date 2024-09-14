package com.liushukov.courseFlow.controllers;

import com.liushukov.courseFlow.dtos.CourseDto;
import com.liushukov.courseFlow.dtos.UpdateCourseDto;
import com.liushukov.courseFlow.models.Course;
import com.liushukov.courseFlow.models.CourseTypeEnum;
import com.liushukov.courseFlow.models.SortingOrderEnum;
import com.liushukov.courseFlow.services.CourseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<Object> courseById(@PathVariable Long courseId) {
        var response = courseService.getCourseById(courseId);
        return (response.isPresent())
                ? ResponseEntity.status(HttpStatus.OK).body(response.get())
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found");
    }

    @GetMapping("/course")
    public ResponseEntity<Object> getCourses(@RequestParam(required = false) String name,
                                             @RequestParam(required = false) String type) {
        if (name != null) {
            var response = courseService.getCourseByName(name);
            return (response.isPresent())
                    ? ResponseEntity.status(HttpStatus.OK).body(response.get())
                    : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found");
        }
        else if (type != null) {
            try {
                CourseTypeEnum courseType = CourseTypeEnum.valueOf(type.toUpperCase());
                var response = courseService.getCoursesByKeyword(courseType);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid course type");
            }
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing required parameter: name or type");
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Course>> allCourses(
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String orderBy,
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
    ) {
        SortingOrderEnum sortingOrderEnum = SortingOrderEnum.valueOf(orderBy.toUpperCase());
        var response = courseService.getAllCourses(sortingOrderEnum, sortBy, pageNumber, pageSize);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/create-course")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Course> createCourse(@Valid @RequestBody CourseDto courseDto) {
        var course = courseService.getCourseByName(courseDto.name());
        if (course.isEmpty()) {
            var response = courseService.saveCourse(courseDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PatchMapping("/update-course/{courseId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Course> updateCourse(
            @PathVariable Long courseId,
            @Valid @RequestBody UpdateCourseDto courseDto
    ) {
        var course = courseService.getCourseById(courseId);
        if (course.isPresent()) {
            var response = courseService.updateCourse(course.get(), courseDto);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/delete-course/{courseId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<String> deleteCourse(@PathVariable Long courseId) {
        var course = courseService.getCourseById(courseId);
        if (course.isPresent()) {
            courseService.deleteCourse(course.get());
            return ResponseEntity.status(HttpStatus.OK).body("The course was successfully deleted");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course was not found");
        }
    }
}
