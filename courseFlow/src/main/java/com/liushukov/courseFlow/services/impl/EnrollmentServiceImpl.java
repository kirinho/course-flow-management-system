package com.liushukov.courseFlow.services.impl;

import com.liushukov.courseFlow.dtos.AssignmentUserResponseDto;
import com.liushukov.courseFlow.models.Course;
import com.liushukov.courseFlow.models.Enrollment;
import com.liushukov.courseFlow.models.User;
import com.liushukov.courseFlow.repositories.EnrollmentRepository;
import com.liushukov.courseFlow.services.EnrollmentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;

    public EnrollmentServiceImpl(EnrollmentRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    public Optional<Enrollment> getEnrollmentByUserAndCourse(User user, Course course) {
        return enrollmentRepository.findEnrollmentByUserAndCourse(user.getId(), course.getId());
    }

    @Override
    public List<AssignmentUserResponseDto> getUsersByCourse(long courseId) {
        return enrollmentRepository.findUsersByCourseId(courseId)
                .stream()
                .map(user -> new AssignmentUserResponseDto(
                        user.getId(),
                        user.getFullName(),
                        user.getEmail()
                )).toList();
    }

    @Override
    public void createEnrollment(User user, Course course) {
        Enrollment enrollment = new Enrollment(user, course);
        enrollmentRepository.save(enrollment);
    }

    @Override
    public void deleteEnrollment(Enrollment enrollment) {
        enrollmentRepository.delete(enrollment);
    }
}
