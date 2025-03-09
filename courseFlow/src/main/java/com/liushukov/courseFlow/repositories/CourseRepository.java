package com.liushukov.courseFlow.repositories;

import com.liushukov.courseFlow.models.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query(value = "SELECT * FROM courses WHERE id = ?1", nativeQuery = true)
    Optional<Course> findCourseById(Long id);

    @Query(value = "SELECT * FROM courses WHERE user_id = ?1", nativeQuery = true)
    Page<Course> findCoursesByManager(Long userId, Pageable pageable);

    @Query(value = "SELECT * FROM courses WHERE enrollment_code = ?1", nativeQuery = true)
    Optional<Course> findCourseByEnrollmentCode(String enrollmentCode);
}
