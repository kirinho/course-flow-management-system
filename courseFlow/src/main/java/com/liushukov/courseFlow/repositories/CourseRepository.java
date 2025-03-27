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

    @Query("""
           SELECT DISTINCT c FROM Course c
           LEFT JOIN c.enrollments e WITH e.user.id = :userId
           WHERE (COALESCE(:name, '') = '' OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
           AND (:flag = false OR e IS NOT NULL)
            """)
    Page<Course> findCoursesByCriteria(@Param("userId") Long userId,
                                       @Param("name") String name,
                                       @Param("flag") boolean flag,
                                       Pageable pageable);
}
