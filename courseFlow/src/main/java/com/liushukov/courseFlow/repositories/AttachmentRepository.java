package com.liushukov.courseFlow.repositories;

import com.liushukov.courseFlow.models.Attachment;
import com.liushukov.courseFlow.models.BaseLessonAssignment;
import com.liushukov.courseFlow.models.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    @Query("SELECT a FROM Attachment a WHERE a.id = :id")
    Optional<Attachment> findAttachmentById(@Param("id") long id);

    @Modifying
    @Query("DELETE FROM Attachment a WHERE a.lessonAssignment = :item")
    void deleteByLessonAssignment(@Param("item") BaseLessonAssignment item);
}
