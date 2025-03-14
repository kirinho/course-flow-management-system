package com.liushukov.courseFlow.repositories;

import com.liushukov.courseFlow.models.Assignment;
import com.liushukov.courseFlow.models.BaseLessonAssignment;
import com.liushukov.courseFlow.models.Lesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BaseLessonAssignmentRepository extends JpaRepository<BaseLessonAssignment, Long> {
    @Query("SELECT bla FROM BaseLessonAssignment bla WHERE bla.id = :blaId")
    Optional<Lesson> findLessonById(@Param("blaId") long blaId);

    @Query("SELECT bla FROM BaseLessonAssignment bla WHERE bla.id = :blaId")
    Optional<Assignment> findAssignmentById(@Param("blaId") long blaId);

    @Query("""
            SELECT bla FROM BaseLessonAssignment bla
            LEFT JOIN FETCH bla.attachments
            WHERE bla.module.id = :moduleId
            ORDER BY bla.position
            """)
    Page<BaseLessonAssignment> findAllByModuleId(@Param("moduleId") long moduleId, Pageable pageable);
}
