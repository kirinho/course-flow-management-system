package com.liushukov.courseFlow.services;

import com.liushukov.courseFlow.dtos.CourseDto;
import com.liushukov.courseFlow.dtos.CourseManagerResponseDto;
import com.liushukov.courseFlow.dtos.CoursePageResponseDto;
import com.liushukov.courseFlow.dtos.CourseResponseDto;
import com.liushukov.courseFlow.models.Course;
import com.liushukov.courseFlow.models.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface CourseService {
    Optional<Course> getCourseById(long id);

    CoursePageResponseDto toCoursePageResponseDto(Course course, User user, boolean enrolled);

    List<CourseResponseDto> getAllCourses(String sortBy, String orderBy, int pageNumber, int pageSize);

    List<CourseManagerResponseDto> getCoursesByManager(long userId, int pageNumber, int pageSize);

    void createCourse(User user, CourseDto courseDto, MultipartFile image) throws IOException;

    void updateCourse(Course course, CourseDto courseDto, MultipartFile image) throws IOException;

    void deleteCourse(Course course);
}
