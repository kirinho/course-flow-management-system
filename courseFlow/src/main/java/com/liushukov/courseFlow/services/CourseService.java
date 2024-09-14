package com.liushukov.courseFlow.services;

import com.liushukov.courseFlow.dtos.CourseDto;
import com.liushukov.courseFlow.dtos.UpdateCourseDto;
import com.liushukov.courseFlow.models.Course;
import com.liushukov.courseFlow.models.CourseTypeEnum;
import com.liushukov.courseFlow.models.SortingOrderEnum;
import com.liushukov.courseFlow.repositories.CourseRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Cacheable(value = "coursesByName", key = "#name")
    public Optional<Course> getCourseByName(String name) {
        return courseRepository.findByName(name);
    }

    @Cacheable(value = "coursesById", key = "#id")
    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    @Cacheable(value = "allCourses", key = "{#orderBy, #sortBy, #pageNumber, #pageSize}")
    public List<Course> getAllCourses(SortingOrderEnum orderBy, String sortBy, int pageNumber, int pageSize) {
        Pageable pageable;
        switch (orderBy) {
            case DESC -> pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).descending());
            default -> pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        }
        return courseRepository.findAll(pageable).getContent();
    }

    public List<Course> getCoursesByKeyword(CourseTypeEnum typeEnum) {
        return courseRepository
                .findAll()
                .stream()
                .filter(course -> course.getTypeEnum() == typeEnum)
                .toList();
    }

    public Course saveCourse(CourseDto courseDto) {
        var course = new Course(
                courseDto.name(),
                courseDto.description(),
                courseDto.type()
        );
        return courseRepository.save(course);
    }

    public Course updateCourse(Course course, UpdateCourseDto courseDto) {
        if (courseDto.name() != null) {
            course.setName(courseDto.name());
        }
        if (courseDto.description() != null) {
            course.setDescription(courseDto.description());
        }
        if (courseDto.type() != null) {
            course.setTypeEnum(courseDto.type());
        }
        return courseRepository.save(course);
    }

    public void deleteCourse(Course course) {
        courseRepository.delete(course);
    }
}
