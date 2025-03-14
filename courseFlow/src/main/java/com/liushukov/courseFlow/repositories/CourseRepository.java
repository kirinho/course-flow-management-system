package com.liushukov.courseFlow.repositories;

import com.liushukov.courseFlow.models.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query("SELECT c FROM Course c WHERE c.id = :courseId")
    Optional<Course> findCourseById(@Param("courseId") Long courseId);

    @Query("SELECT c FROM Course c WHERE c.user.id = :userId")
    Page<Course> findCoursesByManager(@Param("userId") Long userId, Pageable pageable);
    @Query("SELECT c FROM Course c WHERE c.enrollmentCode = :enrollmentCode")
    Optional<Course> findCourseByEnrollmentCode(@Param("enrollmentCode") String enrollmentCode);
}
