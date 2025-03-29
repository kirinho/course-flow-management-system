package com.liushukov.courseFlow.services.impl;

import com.liushukov.courseFlow.configs.CourseCodeGenerator;
import com.liushukov.courseFlow.dtos.*;
import com.liushukov.courseFlow.models.Course;
import com.liushukov.courseFlow.models.Module;
import com.liushukov.courseFlow.models.User;
import com.liushukov.courseFlow.repositories.CourseRepository;
import com.liushukov.courseFlow.repositories.ModuleRepository;
import com.liushukov.courseFlow.services.CourseService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final CourseCodeGenerator courseCodeGenerator;
    private final ModuleRepository moduleRepository;

    public CourseServiceImpl(CourseRepository courseRepository, CourseCodeGenerator courseCodeGenerator, ModuleRepository moduleRepository) {
        this.courseRepository = courseRepository;
        this.courseCodeGenerator = courseCodeGenerator;
        this.moduleRepository = moduleRepository;
    }

    @Cacheable(value = "courses", key = "#id", unless = "#result == null")
    @Override
    public Optional<Course> getCourseById(long id) {
        return courseRepository.findCourseById(id);
    }

    private CourseResponseDto toCourseResponseDto(Course course) {
        return new CourseResponseDto(
                course.getId(),
                course.getName(),
                course.getDescription(),
                Base64.getEncoder().encodeToString(course.getImage()),
                course.getUser().getFullName(),
                course.getUser().getEmail()
        );
    }

    private CourseManagerResponseDto toCourseManagerResponseDto(Course course) {
        return new CourseManagerResponseDto(
                course.getId(),
                course.getName(),
                course.getDescription(),
                course.getEnrollmentCode(),
                Base64.getEncoder().encodeToString(course.getImage()),
                course.getUser()
        );
    }

    @Override
    public CourseOverviewDto getAllModulesWithLessonsAndAssignments(Course course, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<Long> ids = moduleRepository.findModulesIdsByCourse(course.getId(), pageable).getContent();

        List<Module> modules = moduleRepository.findModulesWithLessonsAndAssignments(ids);
        List<ModulePageResponseDto> modulePageResponseDtos = modules.stream().map(
                module -> new ModulePageResponseDto(
                        module.getId(),
                        module.getName(),
                        module.getDescription(),
                        module.getLessonAssignments()
                                .stream()
                                .map(baseLessonAssignment -> new LessonAssignmentPageResponseDto(
                                        baseLessonAssignment.getId(),
                                        baseLessonAssignment.getTitle(),
                                        baseLessonAssignment.getType()
                                )).collect(Collectors.toList())
                )
        ).toList();

        return (!modulePageResponseDtos.isEmpty())
                ? new CourseOverviewDto(
                        course.getId(),
                        course.getName(),
                        course.getDescription(),
                        Base64.getEncoder().encodeToString(course.getImage()),
                        modulePageResponseDtos)
                : new CourseOverviewDto(
                        course.getId(),
                        course.getName(),
                        course.getDescription(),
                        Base64.getEncoder().encodeToString(course.getImage()),
                        Collections.emptyList());
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
    public CourseResponseWrapperDto getAllCourses(String sortBy, String orderBy, int pageNumber, int pageSize,
                                                 String name, boolean flag, long userId) {
        Pageable pageable;
        switch (orderBy) {
            case "desc" -> pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).descending());
            default -> pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).ascending());
        }
        Page<Course> coursePage = courseRepository.findCoursesByCriteria(userId, name, flag, pageable);
        int totalPages = (int) Math.ceil((double) (int) coursePage.getTotalElements() / pageSize);
        return new CourseResponseWrapperDto(
                totalPages,
                coursePage.getContent()
                .stream()
                .map(this::toCourseResponseDto)
                .toList()
        );
    }

    @Override
    public List<CourseManagerResponseDto> getCoursesByManager(long userId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return courseRepository.findCoursesByManager(userId, pageable).getContent().stream().map(this::toCourseManagerResponseDto).toList();
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

    @CacheEvict(value = "courses", key = "#course.id", condition = "#course != null")
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

    @CacheEvict(value = "courses", key = "#course.id", condition = "#course != null")
    @Override
    public void deleteCourse(Course course) {
        courseRepository.delete(course);
    }
}
