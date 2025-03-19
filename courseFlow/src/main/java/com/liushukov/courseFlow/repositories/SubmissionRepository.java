package com.liushukov.courseFlow.repositories;

import com.liushukov.courseFlow.models.Submission;
import com.liushukov.courseFlow.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    @Query("SELECT s FROM Submission s WHERE s.id = :id")
    Optional<Submission> findSubmissionById(@Param("id") long id);

    @Query("""
            SELECT s FROM Submission s
            LEFT JOIN FETCH s.attachments
            LEFT JOIN FETCH s.grade
            WHERE s.assignment.id = :assignmentId
            AND s.student.id = :studentId
            """)
    Optional<Submission> findSubmissionWithAttachmentsAndGrade(@Param("assignmentId") Long assignmentId,
                                                               @Param("studentId") Long studentId);
}
