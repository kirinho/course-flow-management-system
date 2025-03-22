package com.liushukov.courseFlow.repositories;

import com.liushukov.courseFlow.models.Enrollment;
import com.liushukov.courseFlow.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    @Query("SELECT e FROM Enrollment e WHERE e.user.id = :userId AND e.course.id = :courseId")
    Optional<Enrollment> findEnrollmentByUserAndCourse(@Param("userId") Long userId, @Param("courseId") Long courseId);

    @Query("SELECT e.user FROM Enrollment e WHERE e.course.id = :courseId")
    List<User> findUsersByCourseId(@Param("courseId") Long courseId);
}
