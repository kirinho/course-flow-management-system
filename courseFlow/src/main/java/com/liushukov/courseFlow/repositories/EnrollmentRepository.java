package com.liushukov.courseFlow.repositories;

import com.liushukov.courseFlow.models.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    @Query(value = "SELECT * FROM enrollments WHERE user_id = ?1 AND course_id = ?2", nativeQuery = true)
    Optional<Enrollment> findEnrollmentByUserAndCourse(Long userId, Long courseId);
}
