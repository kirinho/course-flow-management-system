package com.liushukov.courseFlow.services;

import com.liushukov.courseFlow.dtos.AssignmentUserResponseDto;
import com.liushukov.courseFlow.models.Course;
import com.liushukov.courseFlow.models.Enrollment;
import com.liushukov.courseFlow.models.User;

import java.util.List;
import java.util.Optional;

public interface EnrollmentService {
    Optional<Enrollment> getEnrollmentByUserAndCourse(User user, Course course);

    List<AssignmentUserResponseDto> getUsersByCourse(long courseId);

    void createEnrollment(User user, Course course);

    void deleteEnrollment(Enrollment enrollment);
}
