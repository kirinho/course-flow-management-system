package com.liushukov.courseFlow.services.impl;

import com.liushukov.courseFlow.configs.CourseCodeGenerator;
import com.liushukov.courseFlow.dtos.CourseDto;
import com.liushukov.courseFlow.dtos.CoursePageResponseDto;
import com.liushukov.courseFlow.dtos.CourseResponseDto;
import com.liushukov.courseFlow.models.Course;
import com.liushukov.courseFlow.models.User;
import com.liushukov.courseFlow.repositories.CourseRepository;
import com.liushukov.courseFlow.services.CourseService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final CourseCodeGenerator courseCodeGenerator;

    public CourseServiceImpl(CourseRepository courseRepository, CourseCodeGenerator courseCodeGenerator) {
        this.courseRepository = courseRepository;
        this.courseCodeGenerator = courseCodeGenerator;
    }

    @Override
    public Optional<Course> getCourseById(long id) {
        return courseRepository.findCourseById(id);
    }

    @Override
    public CourseResponseDto toCourseResponseDto(Course course) {
        return new CourseResponseDto(
                course.getId(),
                course.getName(),
                course.getDescription(),
                Base64.getEncoder().encodeToString(course.getImage()),
                course.getUser()
        );
    }

    @Override
    public CoursePageResponseDto toCoursePageResponseDto(Course course, User user, boolean enrolled) {
        return new CoursePageResponseDto(
                course.getId(),
                course.getName(),
                course.getDescription(),
                Base64.getEncoder().encodeToString(course.getImage()),
                enrolled,
                user.getId()
        );
    }

    @Override
    public List<CourseResponseDto> getAllCourses(String sortBy, String orderBy, int pageNumber, int pageSize) {
        Pageable pageable;
        switch (orderBy) {
            case "desc" -> pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).descending());
            default -> pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).ascending());
        }
        return courseRepository.findAll(pageable).getContent().stream().map(this::toCourseResponseDto).toList();
    }

    @Override
    public List<CourseResponseDto> getCoursesByManager(long userId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return courseRepository.findCoursesByManager(userId, pageable).getContent().stream().map(this::toCourseResponseDto).toList();
    }

    @Override
    public void createCourse(User user, CourseDto courseDto, MultipartFile image) throws IOException {
        Course course = new Course(
                courseDto.name(),
                courseDto.description(),
                image.getBytes(),
                courseCodeGenerator.generateCode(),
                user
        );
        courseRepository.save(course);
    }

    @Override
    public void updateCourse(Course course, CourseDto courseDto, MultipartFile image) throws IOException {
        if (courseDto.name() != null) {
            course.setName(courseDto.name());
        }
        if (courseDto.description() != null) {
            course.setDescription(courseDto.description());
        }
        if (image != null) {
            course.setImage(image.getBytes());
        }
        courseRepository.save(course);
    }

    @Override
    public void deleteCourse(Course course) {
        courseRepository.delete(course);
    }
}
