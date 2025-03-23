package com.liushukov.courseFlow.repositories;

import com.liushukov.courseFlow.dtos.StudentTotalGradeProjection;
import com.liushukov.courseFlow.models.Assignment;
import com.liushukov.courseFlow.models.BaseLessonAssignment;
import com.liushukov.courseFlow.models.Lesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BaseLessonAssignmentRepository extends JpaRepository<BaseLessonAssignment, Long> {
    @Query("SELECT bla FROM BaseLessonAssignment bla WHERE bla.id = :blaId")
    Optional<Lesson> findLessonById(@Param("blaId") long blaId);

    @Query("SELECT bla FROM BaseLessonAssignment bla WHERE bla.id = :blaId")
    Optional<Assignment> findAssignmentById(@Param("blaId") long blaId);

    @Query("""
            SELECT bla FROM  BaseLessonAssignment bla
            LEFT JOIN FETCH bla.attachments
            WHERE bla.id = :lessonId
            """)
    Lesson findLessonWithAttachments(@Param("lessonId") long lessonId);

    @Query("""
            SELECT bla FROM  BaseLessonAssignment bla
            LEFT JOIN FETCH bla.attachments
            WHERE bla.id = :assignmentId
            """)
    Assignment findAssignmentWithAttachments(@Param("assignmentId") long assignmentId);

    @Query("""
        SELECT a
        FROM Assignment a
        LEFT JOIN a.submissions s ON s.student.id = :studentId
        LEFT JOIN s.grade g
        WHERE a.module.course.id = :courseId
        ORDER BY a.module.position, a.position
        """)
    List<Assignment> findAssignmentsByCourseAndStudent(@Param("courseId") long courseId,
                                                       @Param("studentId") long studentId);

    @Query("""
        SELECT bla.id FROM BaseLessonAssignment bla
        WHERE bla.module.id = :moduleId
        ORDER BY bla.position
        """)
    Page<Long> findAllIdsByModuleId(@Param("moduleId") long moduleId, Pageable pageable);

    @Query("""
        SELECT bla FROM BaseLessonAssignment bla
        LEFT JOIN FETCH bla.attachments
        WHERE bla.id IN :ids
        ORDER BY bla.position
        """)
    List<BaseLessonAssignment> findAllByIds(@Param("ids") List<Long> ids);
}
